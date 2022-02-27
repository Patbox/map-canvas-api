# Map Canvas API
Simple, server side api for drawing on maps with runtime only state and no id collisions! It can be used in non-main/server threads for better performance/more fps.
## Usage:
Add it to your dependencies like this:

```
repositories {
	maven { url 'https://maven.nucleoid.xyz' }
}

dependencies {
	modImplementation include("eu.pb4:map-canvas-api:[TAG]")
}
```

Then, you can just create canvas with `DrawableCanvas.create()` and draw on it.
For displaying, you can use `VirtualDisplay` or implement it by yourself.

Just keep in mind it won't clear after you! So add/remove players from them to not send useless packets!
