# Weather App

## Overview
Android-weather-app is an Android application built with Kotlin that provides real-time weather updates based on a searched city, or real device location. The app fetches weather data from an external API and presents it in a clean and user-friendly interface.

## Features
- Display current weather conditions, including temperature, humidity, and wind speed
- Display 3 hour forecast: 5 days (list scrollable left/right)
- Open selected locations precipitation map in browser 
- Show weather by city name
- Show weather automatically by current location
- User-friendly UI with bottom bar navigation
- Light and dark theme options
- Localization Support for English and Finnish

## Screenshots
<img src="https://github.com/user-attachments/assets/2516dc1c-2e72-465b-950d-4492556e1c85" alt=image width=250>
<img src="https://github.com/user-attachments/assets/a3bc383c-4965-436e-a7ee-ba32e220b4ba" alt=image width=250>
<img src="https://github.com/user-attachments/assets/89a9d6f6-2d43-41d8-b899-5303eacac5f1" alt=image width=250>

## Technologies Used
- **Kotlin** - Primary programming language used for development.
- **Jetpack Compose** - Declarative UI toolkit for building modern UIs with Kotlin.
- **Retrofit** - Used for making network API calls and parsing JSON responses.
- **OpenWeatherMap API** - Fetches real-time weather updates based on location.
- **Material 3** - Provides modern UI components based on Material You design principles.
- **Navigation Compose** - Simplifies the management of navigation within a Compose-based UI.
- **Retrofit Gson Converter** - Handles JSON serialization and deserialization when using Retrofit for network operations.
- **DataStore Preferences** - A modern, asynchronous storage solution for simple key-value pairs, replacing SharedPreferences.
- **Coil for Compose** - An image loading library for Compose, used to load and display images efficiently in your app.
- **LocationManager** - Android Location API.
- **Permissions** - ACCESS_COARSE_LOCATION for accessing device location data.
- **State Management & Live Updates** - StateFlow to store and observe the current location, and LocationListener for reacting to changes.

## Installation

1. Clone the repository
2. Open the project in Android Studio.
3. Configure the API key:
   - Obtain an API key from OpenWeatherMap.
   - Create a `apikeys.properties` file in the root directory, that includes the following:
     ```properties
     API_KEY="your_api_key"
     ```
   Replace `your_api_key` with the actual API key you obtain from OpenWeatherMap.
4. Build and run the app on an emulator or physical device.

## Usage

1. Launch the app.
2. Allow location permissions (optional)
3. Enter a city name to fetch weather data, or toggle "Use My Location" on (requires step 2) to automatically fetch weather data from current location
4. View real-time weather conditions and forecasts.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

- **GitHub**: [AkseliTanila](https://github.com/AkseliTanila)
- **Email**: [akke.tanila@gmail.com](mailto:akke.tanila@gmail.com)
