//
//  ImageViewController.swift
//  ImageView
//
//  Created by Felix Nievelstein on 20.10.21.
//

import UIKit
import Combine

fileprivate enum AnimationDirection {
    case ToCenter
    case FromCenter
}

class ImageViewController: UIViewController, UIScrollViewDelegate {

    @IBOutlet weak var backgroundView: UIView!
        
    @IBOutlet var scrollView: UIScrollView!
    
    @IBOutlet weak var contentView: UIView!
    
    @IBOutlet var imageView: UIImageView!
    
    private var cancellables = Set<AnyCancellable>()
    
    var ratioConstraint: NSLayoutConstraint? = nil
    
    /**
     Image that should be displayed
     */
    public var image: UIImage? = nil {
        didSet {
            if let mImageView = imageView {
                mImageView.image = image
            }
        }
    }
    
    public var imageRect: CGRect? = nil
    
    /**
     Indicates if a button should be displayed to dismiss the view controller.
     */
    var showCloseBtn: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setScrollView()
        if showCloseBtn {
            setCloseBtn()
        }
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return UIInterfaceOrientationMask.all
    }

    override var preferredInterfaceOrientationForPresentation: UIInterfaceOrientation {
        return UIInterfaceOrientation.unknown
    }
    
    
    
    private func setScrollView() {
        scrollView.delegate = self
        scrollView.alwaysBounceVertical = false
        scrollView.alwaysBounceHorizontal = false
        scrollView.showsVerticalScrollIndicator = false
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.minimumZoomScale = 1.0
        scrollView.maximumZoomScale = 8.0

        let doubleTapGest = UITapGestureRecognizer(target: self, action: #selector(handleDoubleTapScrollView(recognizer:)))
        doubleTapGest.numberOfTapsRequired = 2
        scrollView.addGestureRecognizer(doubleTapGest)
        
        let swipeGest = UISwipeGestureRecognizer(target: self, action: #selector(closeViewController))
        swipeGest.direction = .down
        scrollView.addGestureRecognizer(swipeGest)

        addImageTo(scrollView)
    }
    
    
    private func addImageTo(_ scrollView: UIScrollView) {
        imageView.image = image
        imageView.contentMode = .scaleAspectFit
        if let mImage = image {
            ratioConstraint = NSLayoutConstraint(item: imageView!, attribute: .width, relatedBy: .equal, toItem: imageView, attribute: .height, multiplier: mImage.size.width / mImage.size.height, constant: 0)
            imageView.addConstraint(ratioConstraint!)
        }
        
    }
    
    private func setCloseBtn() {
        let size: CGFloat = 20
        let closeBtn = UIButton(type: .custom)
        closeBtn.frame = CGRect(x: 0, y: 0, width: size, height: size)
        closeBtn.imageView?.contentMode = .scaleAspectFit
        closeBtn.contentVerticalAlignment = .fill
        closeBtn.contentHorizontalAlignment = .fill
        closeBtn.setImage(UIImage(systemName: "xmark", withConfiguration: UIImage.SymbolConfiguration(weight: .medium)), for: .normal)
        
        
        closeBtn.tintColor = .white
        closeBtn.addTarget(self, action: #selector(closeViewController), for: .touchUpInside)
        closeBtn.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(closeBtn)
        
        let heightConstraint = NSLayoutConstraint(item: closeBtn, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .height, multiplier: 1, constant: size)
        let widthConstraint = NSLayoutConstraint(item: closeBtn, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .width, multiplier: 1, constant: size)
        closeBtn.addConstraint(heightConstraint)
        closeBtn.addConstraint(widthConstraint)
        let topConstraint = NSLayoutConstraint(item: closeBtn, attribute: .top, relatedBy: .equal, toItem: view.safeAreaLayoutGuide, attribute: .top, multiplier: 1, constant: 8)
        view.addConstraint(topConstraint)
        let trailingConstraint = NSLayoutConstraint(item: view.safeAreaLayoutGuide, attribute: .trailing, relatedBy: .equal, toItem: closeBtn, attribute: .trailing, multiplier: 1, constant: 16)
        view.addConstraint(trailingConstraint)
    }
    
    private func prepareAppearAnimation() {
        if imageRect == nil, let mScrollView = scrollView  {
            mScrollView.alpha = 0.0
        }
    }
    
    /**
     Checks if the scrollview is zoomed in. If that is the case zooms out animated.
     */
    private func prepareClose() -> AnyPublisher<Void, Never> {
        return Future<Void, Never> { [weak self] promise in
            if self?.scrollView?.zoomScale != 1 {
                UIView.animate(withDuration: 0.1) {
                    self?.scrollView?.zoomScale = 1
                } completion: { _ in
                    promise(Result.success(Void()))
                }
            } else {
                promise(Result.success(Void()))
            }
        }.eraseToAnyPublisher()
    }
    
    @objc func closeViewController() {
        prepareClose().sink { [weak self] _ in
            guard let strongSelf = self else { return }
            if let mRect = strongSelf.imageRect {
                strongSelf.animate(rect: mRect, direction: .FromCenter)
            }
            UIView.animate(withDuration: 0.25, delay: 0.15) {
                self?.backgroundView.alpha = 0
            } completion: { success in
                self?.dismiss(animated: false, completion: nil)
            }
        }.store(in: &cancellables)
    }
    
    /**
     Zoom the image in or out. Zoom in tries to fill the scrollview with the given image.
     */
    @objc func handleDoubleTapScrollView(recognizer: UITapGestureRecognizer) {
        if scrollView?.zoomScale == 1, let mImage = image {
            UIView.animate(withDuration: 0.2) { [weak self] in
                guard let strongSelf = self else { return }
                strongSelf.scrollView.zoomScale = strongSelf.fillZoomScaleFor(image: mImage)
            }
        } else {
            UIView.animate(withDuration: 0.2) { [weak self] in
                self?.scrollView.zoomScale = 1
            }
        }
    }

    /**
     Calculate the zoom factor to fill the scrollview with the given image.
     */
    private func fillZoomScaleFor(image: UIImage) -> CGFloat {
        // Potraite mode
        if scrollView.frame.height > scrollView.frame.width {
            return (image.size.width / scrollView.frame.width) * scrollView.frame.height / image.size.height
        }
        // Landscape mode
        return (image.size.height / scrollView.frame.height) * scrollView.frame.width / image.size.width
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        prepareAppearAnimation()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if let mImageRect = imageRect {
            animate(rect: mImageRect, direction: .ToCenter)
        }
        
        UIView.animate(withDuration: 0.15, delay: 0.0, options: .curveEaseInOut) { [weak self] in
            self?.backgroundView.alpha = 1.0
            if self?.imageRect == nil {
                self?.scrollView?.alpha = 1.0
            }
        }
    }
        
    private func animate(rect: CGRect, direction: AnimationDirection) {
        if let mImage = image, let mImageView = imageView {
            let yCenter = scrollView!.frame.height / 2
            let scaleFactor = mImageView.frame.width / mImage.size.width
            let scaledImageSize = CGSize(width: mImage.size.width * scaleFactor, height: mImage.size.height * scaleFactor)
            let halfImageHeigth = scaledImageSize.height / 2
            let yPosition = yCenter - rect.origin.y - halfImageHeigth
            
            switch direction {
            case .ToCenter:
                scrollView?.contentOffset = CGPoint(x: 0, y: yPosition)
                UIView.animate(withDuration: 0.3, delay: 0.0, options: .curveEaseInOut) { [weak self] in
                    self?.scrollView?.contentOffset = CGPoint(x: 0, y: 0)
                }
            case .FromCenter:
                scrollView?.contentOffset = CGPoint(x: 0, y: 0)
                UIView.animate(withDuration: 0.3, delay: 0.0, options: .curveEaseInOut) { [weak self] in
                    self?.scrollView?.contentOffset = CGPoint(x: 0, y: yPosition)
                }
            }
        }
    }
    
    func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return contentView
    }
    
}
