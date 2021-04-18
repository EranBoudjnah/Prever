# Prever
>
> A rich preview solution for Android Studio.
>

[![Version - RandomGenKt](https://img.shields.io/maven-central/v/com.mitteloupe.prever/prever?label=Prever+|+MavenCentral)](https://mvnrepository.com/artifact/com.mitteloupe.prever/prever)
[![License](https://img.shields.io/github/license/EranBoudjnah/Prever)](https://github.com/EranBoudjnah/Prever/blob/master/LICENSE)
[![Platform](https://img.shields.io/badge/platform-android-lightgrey)](https://developer.android.com/reference)

[<img src="https://github.com/EranBoudjnah/Prever/raw/master/Assets/Presentation.gif" width="600"/>](Screenshot)

Prever is a Kotlin custom view you can inject into your layout to enrich it in the preview pane of Android Studio.

Prever will tell you when your layouts have no background set, will show you how your TextViews/EditTexts look with text in them, and how much space your ImageViews take.
In run time Prever will have negligible impact on your views.

Prever is short for previewer.

## Install

In your `build.gradle`, add the following:

```groovy
dependencies {
    implementation("com.mitteloupe.prever:prever:1.0.3")
}
```

## Usage

In your layout, simply include the following:

```xml
<include layout="@layout/include_prever" />
```

## Created by
[Eran Boudjnah](https://www.linkedin.com/in/eranboudjnah)

## License
MIT © [Eran Boudjnah](https://www.linkedin.com/in/eranboudjnah)
