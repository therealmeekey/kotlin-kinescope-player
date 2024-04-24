# kotlin-kinescope-player
[![](https://jitpack.io/v/kinescope/kotlin-kinescope-player.svg)](https://jitpack.io/#kinescope/kotlin-kinescope-player)

## Installation

**Step 1.** Add the JitPack repository to your build file.
Add it in your root `build.gradle`/`setting.gradle` file at the end of repositories:

```groovy
dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
      mavenCentral()
      maven { url 'https://jitpack.io' }
   }
}
```

**Step 2.** Add the dependency to your module's `build.gradle` file. Replace `<LATEST_VERSION>` with the current version (can be found in the JitPack badge located at the top of this description):
```groovy
dependencies {
   implementation 'com.github.kinescope:kotlin-kinescope-player:<LATEST_VERSION>'
}
```

## Quick start

### Player setup

1. Add `KinescopePlayerView` to your view's layout

```xml
<io.kinescope.sdk.view.KinescopePlayerView
   android:id="@+id/player_view"
   android:layout_width="match_parent"
   android:layout_height="250dp" />
```

2. Initialize `KinescopeVideoPlayer` instance.

```kotlin
val kinescopePlayer = KinescopeVideoPlayer(context)
```

3. Set your `KinescopePlayer` object to your `KinescopePlayerView` object

```kotlin
val playerView = binding.playerView
   .apply {
      setPlayer(kinescopePlayer)
   }
```

3. Load and play video.

```kotlin
with(kinescopePlayer) {
   loadVideo(liveId, onSuccess = { video ->
      play()
   }, onFailed = {
      it?.printStackTrace()
   })
}
```

### Live

Kinescope supports Live mode. Call the `setLiveState` method to enable Live mode.
In order to check whether the video is a Live broadcast, you can use the `KinescopeVideo.isLive` variable.
Simple example:

```kotlin
with(kinescopePlayer) {
   loadVideo(liveId, onSuccess = { video ->
      if (video.isLive) {
         playerView.setLiveState()
      }
      play()
   }, onFailed = {
      it?.printStackTrace()
   })
}
```

You can also add a display of the start date of the broadcast. To do this, you need to call the `showLiveStartDate` method, passing a date in ISO-8601 format as a parameter. The broadcast start date set in the event settings panel is in the `KinescopeVideo.live.startsAt` variable.

```kotlin
with(kinescopePlayer) {
    loadVideo(liveId, onSuccess = { video ->
        if (video.isLive) {
            playerView.setLiveState()
            video.live?.startsAt?.let { date ->
                playerView.showLiveStartDate(startDate = date)
            }
        }
        play()
    }, onFailed = {
        it?.printStackTrace()
    })
}
```

To hide the display of the start date of the broadcast, use the `hideLiveStartDate` method.

```kotlin
playerView.hideLiveStartDate()
```

### Poster

```kotlin
playerView.showPoster(
    url = POSTER_URL,
    placeholder = R.drawable.placeholder,
    errorPlaceholder = R.drawable.placeholder,
    onLoadFinished = {  }
)
```

You can use the poster set in the event settings panel. The URL is in the `KinescopeVideo.poster.url` variable.

```kotlin
video.poster?.url?.let { posterUrl ->
    playerView.showPoster(
        url = posterUrl,
        placeholder = R.drawable.placeholder,
        errorPlaceholder = R.drawable.placeholder,
        onLoadFinished = {  }
    )
}
```

Hide poster:

```kotlin
playerView.hidePoster()
```

**NOTE!** The poster will be hidden once the video is loaded.

### Custom colors

```kotlin
setColors(
    buttonColor = resources.getColor(R.color.custom_color_res),
    progressBarColor = Color.parseColor("#228B22"),
    scrubberColor = Color.parseColor("#EC3440"),
    playedColor = Color.parseColor("#EBABCF"),
    bufferedColor = Color.YELLOW,
)
```

### Custom button

```kotlin
showCustomButton(
    iconRes = R.drawable.custom_btn_icon,
    onClick = { }
)
```

Hide custom button:

```kotlin
playerView.hideCustomButton()
```

### Fullscreen

For fullscreen feature usage switching player to another view should be implemented in the app side.

1. Add those parametes to "configChanges" field in your apps manifest.xml for orientation changing support:

```xml
<activity android:name=".YourActivity"
        android:configChanges="orientation|screenSize|screenLayout|layoutDirection" />
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

### Analytics

You can set a callback for analytics events. It is called every time any of the events are dispatched. A date object in string format and event name are passed as the arguments.

```kotlin
playerView.setAnalyticsCallback { event, data -> }
```
