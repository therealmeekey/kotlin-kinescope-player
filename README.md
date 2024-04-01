# kotlin-kinescope-player 
[![](https://jitpack.io/v/kinescope/kotlin-kinescope-player.svg)](https://jitpack.io/#kinescope/kotlin-kinescope-player)

## Installation

**Step 1.** Add the JitPack repository to your build file.
Add it in your root build.gradle at the end of repositories:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency
```groovy
dependencies {
   implementation 'com.github.kinescope:kotlin-kinescope-player:$kinescopeVideoPlayerVersion'
}
```

## Usage

### Initialization

Create `KinescopeApiHelper` object and provide a correct API key from your Kinescope account before
starting player usage.

```kotlin
val apiHelper: KinescopeApiHelper = KinescopeApiHelperImpl(RetrofitBuilder.getKinescopeApi("your-api-key-here"))
```

### Player setup

1. Add `KinescopePlayerView` to your view's layout

```xml

<io.kinescope.sdk.view.KinescopePlayerView android:id="@+id/kinescope_player"
    android:layout_width="match_parent" android:layout_height="260dp"
    app:layout_constraintTop_toTopOf="parent" app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

2. Initialize `KinescopeVideoPlayer` instance.

```kotlin
val kinescopePlayer = KinescopeVideoPlayer(this.applicationContext)
```

3. Set your `KinescopePlayer` object to your `KinescopePlayerView` object

```kotlin
playerView.setPlayer(kinescopePlayer)
```

4. Set `KinescopeVideo` object to player and play

```kotlin
kinescopePlayer.setVideo(kinescopeVideo)
kinescopePlayer.play()
```

### Fullscreen feature

For fullscreen feature usage switching player to another view should be implemented in the app side

1. Add those parametes to "configChanges" field in your apps manifest.xml for orientation changing
   support

```xml

<activity android:configChanges="orientation|screenSize|screenLayout|layoutDirection"
    android:name=".YourActivity" />
```

2. Add logic to change target view for player and change flags to make this view fullscreen

```kotlin
private fun setFullscreen(fullscreen: Boolean) { 
    if (fullscreen) { 
        window.setFlags(
           WindowManager.LayoutParams.FLAG_FULLSCREEN, 
           WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        KinescopePlayerView.switchTargetView(playerView, fullscreenPlayerView, kinescopePlayer)  
  
    } else {  
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)  
       
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {  
            window.decorView.systemUiVisibility = 
               (View.SYSTEM_UI_FLAG_FULLSCREEN 
                       and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY 
                       and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)  
        } else { 
            window.decorView.systemUiVisibility = 
               (View.SYSTEM_UI_FLAG_FULLSCREEN 
                       and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) 
        }  
  
        KinescopePlayerView.switchTargetView(fullscreenPlayerView, playerView, kinescopePlayer)  
    }  
}
```

### API requests

You can use built-in `KinescopeApiHelper` class or your own one.

### Logger

For logging network, player and player view events `KinescopeLoggerLevel` can be used.
3 levels of logging are available: `NETWORK`, `PLAYER`, `PLAYER_VIEW`.
