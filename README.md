# Android coding challenge

This project serves as an example of how to build the foundation of a Tiptapp mobile app. It implements a basic background location monitoring service that ensures items are transported or disposed of correctly by the helper (a person with their own vehicle who uses the Tiptapp platform to help others with moving, delivering, and recycling items).

Your task is to continue building on this foundation. Demonstrate your ability to write maintainable code that can support future features while optimizing for the best user experience.

Choose to implement one or both of the following features:

- Create a user interface that allows users to select one or more ads from the endpoint `GET https://api.tiptapp.co/v1/ads` for pickup and transport.
- Modify the location monitoring so that it is active while there are (selected) ads that are less than 10 minutes old (based on their `created` timestamp).
