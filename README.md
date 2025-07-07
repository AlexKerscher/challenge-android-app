# Tiptapp Android Coding Challenge Submission

This project is a comprehensive solution for the Tiptapp Android developer coding challenge. It successfully implements all specified requirements, resulting in a robust, well-tested, and polished application that demonstrates modern Android development best practices.

---

## The Challenge

> This project serves as an example of how to build the foundation of a Tiptapp mobile app. It implements a basic background location monitoring service that ensures items are transported or disposed of correctly by the helper (a person with their own vehicle who uses the Tiptapp platform to help others with moving, delivering, and recycling items).
>
> Your task is to continue building on this foundation. Demonstrate your ability to write maintainable code that can support future features while optimizing for the best user experience.
>
> Choose to implement one or both of the following features:
>
> - Create a user interface that allows users to select one or more ads from the endpoint `GET https://api.tiptapp.co/v1/ads` for pickup and transport.
> - Modify the location monitoring so that it is active while there are (selected) ads that are less than 10 minutes old (based on their `created` timestamp).

---

## Live Demo

![App Demo GIF](assets/demo.gif)

---

## Features Implemented

* **Complete UI Implementation:** A polished, two-column grid UI that fetches and displays ads from the API. The design was inspired by Tiptapp's very own application.
* **Full-Fledged Selection State:** The UI supports multi-item selection with clear, animated visual feedback.
* **Intelligent Location Monitoring:** The background location service is now fully automated. It starts tracking when a user selects an ad created within the last 10 minutes and stops when no such ads are selected.
    * *Note: There is a brief, ~2-second delay after selecting a recent ad before the foreground service notification appears. This is normal behavior for the Android system as it starts the service.*
* **Real-time Distance Calculation:** The app gets an initial location fix to display the distance (in km) from the user to each ad, which updates reactively as the user's location changes.

---

## Architecture & Tech Stack

The application is built using a clean **MVVM (Model-View-ViewModel)** architecture with a **Repository Pattern**. All code is organized by **feature** (`ads`, `location`) for scalability.

* **UI:** 100% Jetpack Compose with Navigation Compose.
* **State Management:** Kotlin Coroutines and `StateFlow`, using the `combine` operator for a fully reactive UI.
* **Networking:** Retrofit & Moshi.
* **Image Loading:** Coil for Compose.
* **Testing:** JUnit 4, MockK, Turbine, and AndroidX Test libraries.

---

## Key Architectural Decisions

1.  **Lean Data Models:** The data models are lean, containing only the fields required by the app. This makes the application resilient to irrelevant backend API changes and follows the YAGNI principle.
2.  **Pragmatic Dependency Injection:** A simple, manual DI approach was chosen to align with the project's scope. For a larger production application, the next logical step would be to migrate to Hilt.
3.  **Refactoring for Testability:** The core business logic was deliberately refactored out of the `Activity` and into the `ViewModel` to enable robust unit testing. Framework-dependent logic was correctly tested with instrumented tests.

---

## Potential Next Steps

This project serves as a strong foundation. Potential future enhancements include:
* Implementing a robust caching strategy with Room to support offline mode.
* Adding pull-to-refresh functionality on the ads screen.
* Enhancing error states with user-facing actions (e.g., a "Retry" button).

---

## How to Run

### Application
1.  Open the project in a recent version of Android Studio.
2.  Build and run the `app` configuration.

### Tests
The project includes a comprehensive test suite.
* **Unit Tests:** Located in `app/src/test`.
* **Instrumented Tests:** Located in `app/src/androidTest`.

Both can be run directly from Android Studio by right-clicking the respective directory and selecting "Run tests...".

---

## Contact

[Alex Kerscher]

* **Email:** [kerscher.souza@gmail.com](mailto:kerscher.souza@gmail.com)
* **LinkedIn:** [linkedin.com/in/alexandre-kerscher-2562b235](https://www.linkedin.com/in/alexandre-kerscher-2562b235)
* **GitHub:** [github.com/AlexKerscher](https://github.com/AlexKerscher)
