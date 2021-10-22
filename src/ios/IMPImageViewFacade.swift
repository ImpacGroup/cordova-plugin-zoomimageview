//
//  IMPImageViewFacade.swift
//  ImageView
//
//  Created by Felix Nievelstein on 19.10.21.
//

import Foundation
import Cordova

@objc (ImpacImageView) class IMPImageViewFacade: CDVPlugin {
    
    @objc(presentImage:) func presentImage(command: CDVInvokedUrlCommand) {
        if command.arguments.count == 1, let infoJson = command.arguments[0] as? String {
            do {
                let decoder = JSONDecoder()
                if let data = infoJson.data(using: String.Encoding.utf8) {
                    let info = try decoder.decode(ImageInfo.self, from: data)
                    let imgViewController = ImageViewController(nibName: "ImageViewController", bundle: nil)
                    imgViewController.modalPresentationStyle = .overFullScreen
                    imgViewController.image = info.image
                    imgViewController.showCloseBtn = info.closeButton
                    imgViewController.imageRect = info.imageRect
                    viewController.present(viewController, animated: false, completion: nil)
                }
            } catch {
                print("cordova-plugin-tracking-transparency: \(error)")
                let result = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: error.localizedDescription)
                self.commandDelegate.send(result, callbackId: command.callbackId)
            }
        }
    }
}
