# Streamify
> _What is the project?_ - The Streamify App is a Netflix inspired app for movies info and possibly streaming with the help of Kotlin and Android Studio

> _What is the MVP ?_ - The minimal viable product is a movie app that perform a network call using retrofit, parsing JSON data and showing movies and tv series to user

> _What are the sprinkles?_ - The sprinkles involves styling the app, adding animation , video play-back for trailer e.t.c

> Live demo [_Stream_](https://appetize.io/app/27optrp2rvsi5c325kmj6mexfi?device=pixel4&osVersion=11.0&scale=75). 

## Table of Contents
* [General Info](#general-information)
* [Built with](#built-with)
* [Features](#features)
* [The Challenge](#the-challenge)
* [Screenshots](#screenshots)
* [Room for Improvement](#room-for-improvement)
* [Acknowledgements](#acknowledgements)
* [What I learned](#what-i-learned)
* [Contact](#contact)

## General Information
- The aim of this project is to provide info to the user. Such info include but not limited to popular movies and Tv series
- Also the purpose of this project is to allow users get info on movie & Tv series details such as cast , genre, similiar e.t.c in real time.

## Built with
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - A flow is an asynchronous version of a Sequence, a type of collection whose values are lazily produced.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 
  - [Room](https://developer.android.com/topic/libraries/architecture/room) - SQLite object mapping library.
  - [Jetpack Navigation](https://developer.android.com/guide/navigation) - Navigation refers to the interactions that allow users to navigate across, into, and back out from the different pieces of content within your app
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.
- [Picasso](https://square.github.io/picasso/) - A powerful image downloading and caching library for Android 
- [Retofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.

## The Challenge

Users should be able to
- Create an account, Sign in / Sign out
- Tap on a movie poster and transition to a details screen with additional information such as:
    - original title
    - movie poster image
    - A plot synopsis
    - User rating, released date and runtime
- To view and play trailers, see the cast of a selected movie, mark a movie as a favorite
- Share the movie trailer link to social media or any other sharing platform

## Features
- Google Sign in/ Sign out Authentication
- Light/dark mode toggle
- Connecting to API
- Navigations (Fragments)
- Display data in to user in a recycler view using Adapter

## Screenshots
![stream](https://user-images.githubusercontent.com/54189037/185766118-a1820395-5f80-4cc2-9f8b-70459e37e6c4.jpg)

## Room for Improvement
- Search Functionality
- Adding Landscape UI and UX
- Language and Notifications set up

## Acknowledgements
 - [Chip Navigation Bar](https://github.com/ismaeldivita/chip-navigation-bar)
 - [Android Youtube Player](https://github.com/PierfrancescoSoffritti/android-youtube-player)
 - [Fading Edge Layout](https://github.com/bosphere/Android-FadingEdgeLayout)
 - [Circular View](https://github.com/hdodenhof/CircleImageView)
 - [Image Slider](https://github.com/denzcoskun/ImageSlideshow)

## What i learned

There were many things that I got in touch for the first time. Like:

- Firebase Google Authentication
- Chip Navigation Bar, Image Slider, Fading Edge ...
- Familiarity with Room, LiveData, ViewModel and Lifecycle
- Familiarity with Picasso and Retrofit

## Contact
Created by [Okolo](https://twitter.com/Okolo_Arthur) - feel free to contact me!


