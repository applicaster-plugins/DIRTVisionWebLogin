//
//  InPlayerWebviewLogin.swift
//  InPlayerWebviewLogin
//
//  Created by Pablo Rueda on 08/10/2018.
//  Copyright © 2018 Applicaster. All rights reserved.
//

import Foundation
import WebKit
import ZappLoginPluginsSDK
import ZappPlugins

public class InPlayerWebviewLogin: NSObject, ZPLoginProviderUserDataProtocol, ZPAppLoadingHookProtocol {

    public var configurationJSON: NSDictionary?
    
    private var storage = Storage()
    private var apiClient = APIClient()

    //MARK: ZPLoginProviderProtocol

    public required override init() {
        super.init()
    }

    public required init(configurationJSON: NSDictionary?) {
        self.configurationJSON = configurationJSON
    }

    public func login(_ additionalParameters: [String : Any]?, completion: @escaping ((ZPLoginOperationStatus) -> Void)) {
        guard let navigationDelegate = ZAAppConnector.sharedInstance().navigationDelegate else {
            return
        }

        let inPlayerWebViewController = InPlayerWebViewController(urlString: Constants.loginURL + Constants.redirectURI,
                                                                  storage: storage, completion: completion)
        navigationDelegate.present(inPlayerWebViewController, presentationType: .present, animated: true)
    }

    public func logout(_ completion: @escaping ((ZPLoginOperationStatus) -> Void)) {
        storage.clean()
        completion(.completedSuccessfully)
    }

    public func isAuthenticated() -> Bool {
        return !getUserToken().isEmpty && !isTokenExpired()
    }

    public func isPerformingAuthorizationFlow() -> Bool {
        return false
    }

    public func getUserToken() -> String {
        return storage.accessToken ?? ""
    }

    //MARK: ZPLoginProviderUserDataProtocol

    public func isUserComply(policies:[String: NSObject]) -> Bool {
        if let free = policies["free"] as? Bool, free == true {
            return true
        }else {
            return isAuthenticated()
        }
    }

    //MARK: ZPAppLoadingHookProtocol

    public func executeOnLaunch(completion: (() -> Void)?) {
        guard let refreshToken = storage.refreshToken else {
            return
        }
        
        apiClient.refreshToken(refreshToken) { (accessToken, refreshToken) in
            if let accessToken = accessToken, let refreshToken = refreshToken {
                self.storage.accessToken = accessToken
                self.storage.refreshToken = refreshToken
                self.storage.tokenDate = Date()
            }
        }
        
        if let completion = completion {
            completion()
        }
    }

    //MARK: Other methods

    func isTokenExpired() -> Bool {
        if let tokenDate = storage.tokenDate {
            let expirationDate = tokenDate.addingTimeInterval(Constants.expirationTime)
            return Date() >= expirationDate
        }
        return true
    }
}
