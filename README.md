# NativeHtml

Renders HTML to native Android components. Easily extensible with custom native elements.

The main use case for this library is rendering HTML-formatted text or (custom) components in cases where TextView HTML capabilities are too limited and WebView is too heavyweight or not suitable for other reasons. 

## Use Cases

 * Formatted help pages with links directly into the application
 * Rendering server-provided content with custom native elements
 * Multiple components with HTML content in a single view where multiple WebView instances would be prohibitive in terms of resource consumption 

## Limitations

 * CSS is mostly limited to CSS 2
 * Floating elements are not supported
 * Formatted text is rendering using built-in TextViews with spans, which means that justified text is not supported.
 
## Extensibility

 * To implement custom elements, extend `AndroidPlatform` and override `createElement`. Native Android views can be wrapped in `AndroidWrapperElement`
 * Platform-independent and platform-specific parts of the code base are clearly separated, so it should be straightforward to port the code to any platform that supports Java and provides a component for formatted text. 
 * Layout managers are separated from containers. So while flexbox is currently not supported, it should be relatively straightforward to implement in a way similar to the existing `BlockLayout` and `TableLayout` classes.
 
## Usage

For a simple example, please refer to the [demo](
https://github.com/stefanhaustein/nativehtml/blob/master/demo-android/src/main/java/org/kobjects/nativehtml/demo/android/MainActivity.java)

## Gradle

Jitpack for the win!

Step 1: Add jitpack to your root build.gradle at the end of repositories:

    allprojects {
		    repositories {
			  ...
			  maven { url 'https://jitpack.io' }
		    }
	    }

Step 2: Add the HtmlView2 dependency

	dependencies {
		compile 'com.github.stefanhaustein.nativehtml:android:v1.0.2'
	}
