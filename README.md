# README #

### Cordova plugin for iOS and Android that allows to display an image in fullscreen and zoom in and out. ###

These plugins adds a function to open a new screen to display an image. The image can be zoomed in and out. It works in landscape and portaite orientation.
If the image already is on the previous screen, you can optionaly add the frame informations to get an smooth between the screens.

### Supported platforms ###
- Android 7+
- iOS 13+
- Cordova iOS 6+
- Cordova Android 10+

### Install ###
To install the cordova plugin perform:
'cordova plugin add cordova-plugin-zoomimageview'

### Immplementation ###

For android you need to make sure that koltin is acitvated in your cordova config.xml.
To these add the following:

```
    <preference name="GradlePluginKotlinEnabled" value="true" />
    <preference name="GradlePluginKotlinCodeStyle" value="official" />
    <preference name="GradlePluginKotlinVersion" value="1.6.0-RC" />
```

The plugin consists of only one function. 
Next you see a declaration of the plugins api for typescript.

```ts
    declare var window: {
        plugins: {
            zoomimageview: {
                presentImage: (
                    info: string | undefined,
                    // With the callback the plugin will inform you when the screen starts closing the screen and when the screen did close.
                    callback: (result: string) => void,
                    errorCallback: (error: any) => void
                ) => void
            }
        }
    };
```

The plugin expects info to be a json string which contains an ImageInfo object. 
The definition of the object should look like this:

```ts
export interface ImageInfo {
    
    // base64 string of the image that should be displayed. Note that only .png or .jpeg files are supported.
    image: string;
        
    // Defines if a close button should be displayed on the plugins screen. 
    closeButton: boolean;
    
    // only needed if you want a transition between the image of the current screen and the plugins screen. 
    imageRect: Rect | undefined;
}

export interface Rect {
    x: number;
    y: number;
    width: number;
    height: number;
}
``` 

The whole implemtation than could look like this:

```ts

    public show(image: string) {
    
        const rect: Rect = {
            x: this.imageControlElement.nativeElement.offsetLeft,
            y: this.imageControlElement.nativeElement.offsetTop,
            width: this.imageControlElement.nativeElement.width,
            height: this.imageControlElement.nativeElement.height
        };
        
        const info: ImageInfo = {
            image: image,
            closeButton: true,
            imageRect: rect
        };
        
        window.plugins.zoomimageview.presentImage(JSON.stringify(info), (result) => {
            console.log(result);
        }, (error) => {
            console.log(error);
        });
    }         
 
```


