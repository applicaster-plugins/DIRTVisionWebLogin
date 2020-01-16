//
//  InPlayerWebViewController.swift
//  InPlayerWebViewController
//
//  Created by Pablo Rueda on 08/10/2018.
//  Copyright © 2018 Applicaster. All rights reserved.
//

import Foundation
import WebKit
import ZappLoginPluginsSDK

public class InPlayerWebViewController: UIViewController {
    typealias Completion = (ZPLoginOperationStatus) -> Void
    
    var loginCompletion: Completion!
    var urlString: String?
    var webview: WKWebView?
    @IBOutlet weak var closeButton: UIButton!
    
    private var storage: Storage!
    
    init(urlString: String, storage: Storage, completion: @escaping Completion) {
        let bundle = Bundle.init(for: InPlayerWebViewController.self)
        super.init(nibName:nil, bundle:bundle)
        self.urlString = urlString
        self.storage = storage
        self.loginCompletion = completion
    }
    
    required public init?(coder aDecoder: NSCoder) {
        return nil
    }
    
    public override func viewDidLoad() {
        view.backgroundColor = .green
        
        let webview = FullScreenWKWebView(frame: view.bounds)
        addScriptTo(webview)
        view.insertSubview(webview, belowSubview:closeButton)
        webview.translatesAutoresizingMaskIntoConstraints = false
        webview.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        webview.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        webview.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        webview.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        
        webview.backgroundColor = .black
        webview.scrollView.backgroundColor = .black
        webview.isOpaque = false
        webview.navigationDelegate = self
        
        if let urlString = urlString, let url = URL(string:urlString) {
            webview.load(URLRequest(url:url))
        }
    }
    
    @IBAction func tapClose() {
        dismiss(animated: true, completion: nil)
        loginCompletion(.cancelled)
    }
    
    private func addScriptTo(_ webview: FullScreenWKWebView) {
        let source: String = "var meta = document.createElement('meta');" +
            "meta.name = 'viewport';" +
            "meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';" +
            "var head = document.getElementsByTagName('head')[0];" + "head.appendChild(meta);";
        
        let script: WKUserScript = WKUserScript(source: source, injectionTime: .atDocumentEnd, forMainFrameOnly: true)
        webview.configuration.userContentController.addUserScript(script)
    }
}

extension InPlayerWebViewController: WKNavigationDelegate {
    
    public func webView(_ webView: WKWebView,
                          decidePolicyFor navigationAction: WKNavigationAction,
                          decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        if let url = navigationAction.request.url, url.absoluteString.starts(with: Constants.redirectURI) {
            let accessToken = getQueryStringParameter(url: url.absoluteString, param: Constants.inplayerTokenParameter)
            let refreshToken = getQueryStringParameter(url: url.absoluteString, param: Constants.inplayerRefreshTokenParameter)
            storage.accessToken = accessToken
            storage.refreshToken = refreshToken
            storage.tokenDate = Date()
            decisionHandler(.cancel)
            dismiss(animated: true, completion: {
                self.loginCompletion(.completedSuccessfully)
            })
        }else {
            decisionHandler(.allow)
        }
    }
    
    func getQueryStringParameter(url: String, param: String) -> String? {
        guard let url = URLComponents(string: url) else { return nil }
        return url.queryItems?.first(where: { $0.name == param })?.value
    }
}
