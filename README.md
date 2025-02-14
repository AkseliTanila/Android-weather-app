# Weather App

## Overview
Android-weather-app is an Android application built with Kotlin that provides real-time weather updates based on a searched city. The app fetches weather data from an external API and presents it in a clean and user-friendly interface.

## Features
- Display current weather conditions, including temperature, humidity, and wind speed
- Display 3 hour forecast: 5 days (list scrollable left/right)
- Open selected locations precipitation map in browser 
- Search for weather updates by city name
- Automatic location-based weather updates (not yet implemented)
- User-friendly UI
- Light and dark theme options
- Localization Support for English and Finnish

## Screenshots
<img src="https://github.com/user-attachments/assets/1202cdd8-99c7-4d7e-8f2d-8dabdfcb9355" alt=image width=300>
<img src="https://github.com/user-attachments/assets/256e6d46-350b-4a80-b0fd-84f94c363333" alt=image width=300>
<img src="https://github.com/user-attachments/assets/4d03633c-2747-44bb-90b3-7882746b927e" alt=image width=300>

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
2. Enter a city name to fetch weather data.
3. View real-time weather conditions and forecasts.

## Contact
For any issues or suggestions, feel free to contact me:

- **GitHub**: [AkseliTanila](https://github.com/AkseliTanila)
- **Email**: [akke.tanila@gmail.com](mailto:akke.tanila@gmail.com)
