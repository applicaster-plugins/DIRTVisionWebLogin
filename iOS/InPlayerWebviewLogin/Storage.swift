//
//  Storage.swift
//  InPlayerWebViewController
//
//  Created by Pablo Rueda on 08/10/2018.
//  Copyright © 2018 Applicaster. All rights reserved.
//

import Foundation

public class Storage {
    var accessToken: String? {
        get {
            return UserDefaults.standard.string(forKey: Constants.userDefaultsAccessTokenKey)
        }
        set {
            UserDefaults.standard.set(newValue, forKey: Constants.userDefaultsAccessTokenKey)
        }
    }
    
    var refreshToken: String? {
        get {
            return UserDefaults.standard.string(forKey: Constants.inplayerRefreshTokenParameter)
        }
        set {
            UserDefaults.standard.set(newValue, forKey: Constants.inplayerRefreshTokenParameter)
        }
    }
    
    var tokenDate: Date? {
        get {
            return UserDefaults.standard.object(forKey: Constants.userDefaultsTokenDateKey) as? Date
        }
        set {
            UserDefaults.standard.set(newValue, forKey: Constants.userDefaultsTokenDateKey)
        }
    }
    
    func clean() {
        let userDefaults = UserDefaults.standard
        userDefaults.removeObject(forKey: Constants.userDefaultsAccessTokenKey)
        userDefaults.removeObject(forKey: Constants.userDefaultsRefreshTokenKey)
        userDefaults.removeObject(forKey: Constants.userDefaultsTokenDateKey)
    }
}
