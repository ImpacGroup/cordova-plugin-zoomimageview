/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Wait for the deviceready event before using any of Cordova's device APIs.
// See https://cordova.apache.org/docs/en/latest/cordova/events/events.html#deviceready
document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady() {
    // Cordova is now initialized. Have fun!

    console.log('Running cordova-' + cordova.platformId + '@' + cordova.version);
    document.getElementById('deviceready').classList.add('ready');
}

async function load(name) {
    return fetch(name).then(response => {
        if (!response.ok) {
          throw new Error('Network response was not OK');
        }
        return response.blob();
      });
}

async function onImgClick() {
    console.log(onImgClick);
    let image = await load("./img/logo.png")

    const reader = new FileReader();
    reader.onloadend = () => {
        console.log("Loaded");
        const base64data = reader.result;
        let rect = {
            x: 0,
            y: 0,
            width: 170,
            height: 200
        }
    
        let imageInfo = {
            image: base64data,
            closeButton: true,
            imageRect: rect
        };  
        console.log(imageInfo);
        window.plugins.zoomimageview.presentImage(JSON.stringify(imageInfo), (result) => {
            console.log(result);
        });
    };
    reader.readAsDataURL(image);
    
}
