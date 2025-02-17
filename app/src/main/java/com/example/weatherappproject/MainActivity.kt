package com.example.weatherappproject

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherappproject.ui.theme.FinalProjectWeatherAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get permissions to use device location / check if permissions are given
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
        }

        setContent {
            // Set theme style based of saved settings
            val context = LocalContext.current
            val isDarkTheme = produceState(initialValue = false) {
                val itemClass = StoredItemsClass(context)
                itemClass.getStoredValues().collect { data ->
                    value = data.second
                }
            }
            themeViewModel.setTheme(isDarkTheme.value)

            FinalProjectWeatherAppTheme(darkTheme = themeViewModel.isDarkTheme.value) {
                MainView(themeViewModel)
            }
        }
    }
}

// Get api key from apikeys.properties
val myApiKey = BuildConfig.API_KEY

// Data Store
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "items_data")
class StoredItemsClass(private val context: Context) {
    private val cityNameKey = stringPreferencesKey("city_name")
    private val isDarkThemeKey = booleanPreferencesKey("is_dark_theme")
    private val fetchByLocationKey = booleanPreferencesKey("fetch_by_location")

    suspend fun saveValues(cityName: String, isDarkTheme: Boolean, fetchByLocation: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[cityNameKey] = cityName
            preferences[isDarkThemeKey] = isDarkTheme
            preferences[fetchByLocationKey] = fetchByLocation
        }
    }

    fun getStoredValues(): Flow<Triple<String, Boolean, Boolean>> {
        return context.dataStore.data.map { preferences ->
            val cityName = preferences[cityNameKey] ?: "Tampere"
            val isDarkTheme = preferences[isDarkThemeKey] ?: false
            val fetchByLocation = preferences[fetchByLocationKey] ?: false
            Triple(cityName, isDarkTheme, fetchByLocation)
        }
    }
}

// Retrofit implementation for fetching weather data
data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherData>,
    val city: City
)

data class WeatherData(
    val dt: Long,
    val main: MainData,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: Sys,
    val dt_txt: String
)

data class MainData(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val seaLevel: Int,
    val grndLevel: Int,
    val humidity: Int,
    val tempKf: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Sys(
    val pod: String
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lat: Double,
    val lon: Double
)

interface ApiService{
    // Fetch weather data by coordinates (lat, lon)
    @GET("forecast")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = myApiKey
    ): WeatherResponse

    // Fetch weather data by city name
    @GET("forecast")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = myApiKey
    ): WeatherResponse
}

object RetrofitInstance{
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

// Main
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(themeViewModel: ThemeViewModel, locationViewModel: LocationViewModel = viewModel()){
    val navController = rememberNavController()
    var forecast by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var storedData by remember { mutableStateOf(Triple("Tampere", false, false)) }
    val context = LocalContext.current
    val itemClass = remember { StoredItemsClass(context) }
    val locationCoordinates by locationViewModel.locationData.collectAsState()

    // Initial settings
    LaunchedEffect(Unit) {
        itemClass.getStoredValues().collect { data ->
            val cityName = if (data.first.isEmpty()) "Tampere" else data.first
            storedData = Triple(cityName, data.second, data.third)
            themeViewModel.setTheme(data.second)
        }
    }

    // Initial location update
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationViewModel.startLocationListener()
        }
    }

    // Initial weather data fetch
    LaunchedEffect(storedData.first, locationCoordinates, storedData.third) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                // Fetch data with coordinates, if feature is on, and location has been updated successfully, otherwise fetch by city
                if (storedData.third && locationCoordinates.first != 0.0 && locationCoordinates.second != 0.0) {
                    forecast = RetrofitInstance.apiService.getWeatherByCoordinates(locationCoordinates.first, locationCoordinates.second)
                } else {
                    forecast = RetrofitInstance.apiService.getWeatherByCity(storedData.first)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text= stringResource(R.string.weather_app), color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeView(navController, forecast, isLoading) }
                composable("details") { DetailsView(navController) }
                composable("settings") { SettingsView(context, navController, themeViewModel, itemClass, storedData) }
            }
        }
    )
}


@Composable
fun BottomNavBar(navController: NavHostController){
    val navItemColor = MaterialTheme.colorScheme.onSecondary
    NavigationBar(
        modifier = Modifier
            .height(70.dp),
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        // About navigation button
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "details", modifier = Modifier.size(30.dp), tint = navItemColor) },
            label = {Text(text = stringResource(R.string.about_nav_title), fontSize = 9.sp, color = navItemColor)},
            selected = navController.currentDestination?.route == "details",
            onClick = {navController.navigate("details") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }},
        )
        // Home navigation button
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "home", modifier = Modifier.size(30.dp), tint = navItemColor) },
            label = {Text(text = stringResource(R.string.home_nav_title), fontSize = 9.sp, color = navItemColor)},
            selected = navController.currentDestination?.route == "home",
            onClick = {navController.navigate("home"){
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }},
        )
        // Settings navigation button
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "settings", modifier = Modifier.size(30.dp), tint = navItemColor) },
            label = {Text(text = stringResource(R.string.settings_nav_title), fontSize = 9.sp, color = navItemColor)},
            selected = navController.currentDestination?.route == "settings",
            onClick = {navController.navigate("settings"){
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }},
        )
    }
}

// ViewModel to toggle theme between light and dark mode
class ThemeViewModel : ViewModel() {
    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme: State<Boolean> = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setTheme(isSetToDarkTheme: Boolean) {
        _isDarkTheme.value = isSetToDarkTheme
    }
}

// ViewModel to get device location by coordinates
class LocationViewModel(application: Application) : AndroidViewModel(application), LocationListener {

    private val _locationData = MutableStateFlow(Pair(0.0, 0.0))
    val locationData: StateFlow<Pair<Double, Double>> = _locationData

    private val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Start location updates when permission is granted
    @SuppressLint("MissingPermission")
    fun startLocationListener() {
        val context = getApplication<Application>().applicationContext
        val hasPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0, 0f, this)
            } catch (securityException: SecurityException) {
                Toast.makeText(getApplication(), "No permission to access location", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permission not granted!", Toast.LENGTH_SHORT).show()
        }
    }

    // Stop location updates when no longer needed
    fun stopLocationUpdates() {
        locationManager.removeUpdates(this)
    }

    // This function is called when location updates happen
    override fun onLocationChanged(location: Location) {
        _locationData.value = Pair(location.latitude, location.longitude)
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}

