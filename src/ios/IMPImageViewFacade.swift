//
//  IMPImageViewFacade.swift
//  ImageView
//
//  Created by Felix Nievelstein on 19.10.21.
//

import Foundation

@objc (Zoomimageview) class IMPImageViewFacade: CDVPlugin, ImageViewControllerDelegate {
    
    private var onPresentCallbackId: String?
    
    @objc(presentImage:) func presentImage(command: CDVInvokedUrlCommand) {
        if command.arguments.count == 1, let infoJson = command.arguments[0] as? String {
            onPresentCallbackId = command.callbackId
            do {
                let decoder = JSONDecoder()
                if let data = infoJson.data(using: String.Encoding.utf8) {
                    let info = try decoder.decode(ImageInfo.self, from: data)
                    let imgViewController = ImageViewController(nibName: "ImageViewController", bundle: nil)
                    imgViewController.modalPresentationStyle = .overFullScreen
                    imgViewController.image = info.image
                    imgViewController.showCloseBtn = info.closeButton
                    imgViewController.delegate = self
                    
                    if let rect = info.imageRect?.toCGRect() {
                        let statusBarHeight = viewController.view.window?.windowScene?.statusBarManager?.statusBarFrame.height ?? 0
                        var finalRect = rect
                        finalRect.origin.y = finalRect.origin.y + statusBarHeight
                        imgViewController.imageRect = finalRect
                    }
                    
                    viewController.present(imgViewController, animated: false, completion: nil)
                }
            } catch {
                print("cordova-plugin-tracking-transparency: \(error)")
                let result = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: error.localizedDescription)
                self.commandDelegate.send(result, callbackId: command.callbackId)
            }
        }
    }
    
    func willClose() {
        if let callbackId = onPresentCallbackId {
            let result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "willClose")
            result?.keepCallback = true
            self.commandDelegate.send(result, callbackId: callbackId)
        }
        
    }
    
    func didClose() {
        if let callbackId = onPresentCallbackId {
            let result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "didClose")
            self.commandDelegate.send(result, callbackId: callbackId)
            onPresentCallbackId = nil
        }
    }
}
