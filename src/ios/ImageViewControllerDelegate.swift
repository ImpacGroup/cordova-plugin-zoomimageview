//
//  ImageViewControllerDelegate.swift
//  ImageView
//
//  Created by Felix Nievelstein on 25.10.21.
//

import Foundation

protocol ImageViewControllerDelegate: AnyObject {
    
    func willClose()
    
    func didClose()
    
}
