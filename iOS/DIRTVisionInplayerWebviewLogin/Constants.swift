//
//  Constants.swift
//  InPlayerWebviewLogin
//
//  Created by Pablo Rueda on 24/09/2019.
//

import Foundation

struct Constants {
    static let userDefaultsAccessTokenKey = "InPlayerWebviewLogin.userDefaultsAccessTokenKey"
    static let userDefaultsRefreshTokenKey = "InPlayerWebviewLogin.userDefaultsRefreshTokenKey"
    static let userDefaultsTokenDateKey = "InPlayerWebviewLogin.userDefaultsTokenDateKey"
    static let expirationTime: TimeInterval = 30 * 3600 * 24 //30 days expressed in seconds
    static let loginURL = "https://dirtvision.inplayer.com/?redirect_uri="
    static let redirectURI = "https://www.app.app"
    static let inplayerTokenParameter = "inplayer_token"
    static let inplayerRefreshTokenParameter = "inplayer_refresh_token"
    
}

struct APIClientConstants {
    static let authenticateURL = "https://services.inplayer.com/accounts/authenticate"
    static let clientIdBodyKey = "client_id"
    static let clientIdBodyValue = "62193f14-6fec-45c4-879f-e0fcf0dac676"
    static let grantTypeBodyKey = "grant_type"
    static let grantTypeBodyValue = "refresh_token"
    static let refreshTokenBodyKey = "refresh_token"
    static let accessTokenResponseParameter = "access_token"
    static let refreshTokenResponseParameter = "refresh_token"
    
}
