//
//  ViewController-Extension.swift
//  ImageView
//
//  Created by Felix Nievelstein on 20.10.21.
//

import Foundation
import UIKit

extension ViewController {
    
    func present(
        image: UIImage,
        rect: CGRect? = nil,
        closeBtn: Bool = true
    ) {
        let viewController = ImageViewController(nibName: "ImageViewController", bundle: nil)
        viewController.modalPresentationStyle = .overFullScreen
        viewController.image = image
        viewController.showCloseBtn = closeBtn
        viewController.imageRect = rect
        present(viewController, animated: false, completion: nil)
    }
}
