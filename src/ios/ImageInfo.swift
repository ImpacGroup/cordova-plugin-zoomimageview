//
//  ImageInfo.swift
//  ImageView
//
//  Created by Felix Nievelstein on 22.10.21.
//

import Foundation
import UIKit

struct ImageInfo: Decodable {
    
    let image: UIImage
    let closeButton: Bool
    let imageRect: CGRect?
    
    enum CodingKeys: String, CodingKey {
        case image
        case closeButton
        case imageRect
    }
    
    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        closeButton = try values.decode(Bool.self, forKey: .closeButton)
        imageRect = try? values.decode(CGRect.self, forKey: .imageRect)
        let imageString = try values.decode(String.self, forKey: .image)
        if let mImage = ImageInfo.imageForBase64String(imageString) {
            image = mImage.withRenderingMode(.alwaysOriginal)
        } else {
            image = UIImage()
        }
    }
    
    static func imageForBase64String(_ strBase64: String) -> UIImage? {
        do{
            let imageData = try Data(contentsOf: URL(string: strBase64)!)
            let image = UIImage(data: imageData)
            return image!
        }
        catch{
            return nil
        }
    }
}
