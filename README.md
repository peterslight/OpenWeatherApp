## Brief

### Android app which will enable the user to search for weather data using open weather map API, get info on weather for seleceted cites for today, and the next 1 week.

Core Task

All of the information should be retrieved from OpenWeatherMap APIs
By default the main screen should show weather data for your current location 
Implement an option to search for a specific location by city name.
option to add the last searched location to Db and show persist on app restart.
User should be informed if the internet connection is lost
Download Apk

https://github.com/peterslight/OpenWeatherApp/blob/main/app/release/app-release.apk

Apk folder

https://github.com/peterslight/OpenWeatherApp/blob/main/app/release

How To Run

clone project
[signup](https://home.openweathermap.org/users/sign_in) with OpenWeatherMap, then head to profile -> [My Api Keys](https://home.openweathermap.org/api_keys).
generate and activate a free key. NB it takes a while to be activated
Inside the project open the SearchRepositoryImpl file. you have 2 options
FAST add the api key directly in the service.searchWeatehr::apiKey param (same for other functions).
RECOMMENDED add the api key to your environmemnt variables, and call it using System.getenv(yourApiKey) find out how to add environment variables [Here](https://chlee.co/how-to-setup-environment-variables-for-windows-mac-and-linux/)
Build/Sync gradle files to download dependencies. NB project uses JDK-11 and the min android sdk is Sdk-24
Tech Stack

This project follows a modularized approach:

Option	Tech
Language	Kotlin
Arch Pattern	MVVM
Network	Retrofit
Android Arch	Single Activity Nav
Theme	Material3
Converter	GSON
Network	Retrofit/OkHttp
Db	Room Db
MultiThreading	RxKotlin
Dependency Injection	Dagger
Imaging	Glide
Tests	Spek Framework
Mock	Mockk
JDK	JDK 11
Tests can be found in respective module tests packages

License

[MIT](https://choosealicense.com/licenses/mit/)
