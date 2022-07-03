# Snacky

[![](https://jitpack.io/v/behnamnasehi/snacky.svg)](https://jitpack.io/#behnamnasehi/snacky)

Snacky inform users of a process that an app has performed or will perform. They appear temporarily, towards the bottom of the screen

## Installation

### Step 1 :

Add the JitPack repository to your build file 

Add it in your root build.gradle at the end of repositories:

```bash
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
### Step 2 :

Add the dependency

```bash
dependencies {
	implementation 'com.github.behnamnasehi:snacky:lastest'
}
```

## implementation
First create your instance and give name to it :

### Default :
```java
Snacky.createInstance(this);
```

### Custom :
```java
Snacky.createInstance(this, "Error", false, new DesignBuilder.Builder()
              .setSubtitleTextColor(R.color.white)
              .setSubtitleTextSize(12)
              .setTitleTextColor(R.color.white)
              .setTitleTextSize(16)
              .setUndoTitleTextColor(R.color.white)
              .setUndoTextSize(14)
              .isRtl(false)
              .setBackgroundColor(R.color.black)
              .setSubtitleTypeface("PATH-TO-YOUR-FONT")
              .setTitleTypeface("PATH-TO-YOUR-FONT")
              .setUndoTypeface("PATH-TO-YOUR-FONT")
              .setBackgroundRadius(12)
              .build());
```

and add view to your activity :

```java
addContentView(
	Snacky.getInstance("Error"),
	LayoutHelper.createFrame(
		LayoutHelper.MATCH_PARENT,
		LayoutHelper.WRAP_CONTENT,
		Gravity.BOTTOM | Gravity.START,
		20, 0, 20, 8
	)
);
```

## Usage


### Default :
```java
Snacky.getInstance().make(
	"Something went wrong !",
	"Please try again and send this requet again to server for test",
	Snacky.LENGTH_LONG,
	"Done"
).start();
```

### Custom :
```java
Snacky.getInstance("Error").make(
	"Something went wrong !",
	"Please try again and send this requet again to server for test",
	Snacky.LENGTH_LONG,
	"Done"
).start();
```

## Contact Me 

Linkedin: [Click Here](https://www.linkedin.com/in/behnamnasehi/)
