# E-ink Component Library for Android

This library provides a set of ready-to-use UI components optimized for e-ink displays on Android. These components are designed to minimize screen flickering, conserve energy, and provide a readable interface.

## Features

* **E-ink Optimized:** Components are designed with the limitations of e-ink displays in mind, such as slow refresh rates and limited color palettes (or grayscale).
* **Flicker Minimization:** Implementation that minimizes the need for full screen refreshes, reducing flickering.
* **Energy Efficiency:** Design focused on efficient resource utilization to minimize energy consumption.
* **Readable Interface:** Components designed with readability in mind, even in challenging lighting conditions.

## Installation

To install the library, add the dependency to your module's `build.gradle` file:

```gradle
dependencies {
    implementation("com.mudita:mmd-core:${mmd-version}")
}
