//
//  FullScreenWKWebView.swift
//  InPlayerWebviewLogin
//
//  Created by Pablo Rueda on 24/09/2019.
//

import Foundation
import WebKit

class FullScreenWKWebView: WKWebView {
    override var safeAreaInsets: UIEdgeInsets {
        return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
}
