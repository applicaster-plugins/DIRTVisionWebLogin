//
//  APIClient.swift
//  InPlayerWebviewLogin
//
//  Created by Pablo Rueda on 08/10/2018.
//  Copyright © 2018 Applicaster. All rights reserved.
//

import Foundation

public class APIClient {
    
    public func refreshToken(_ refreshToken: String, completion: @escaping ((String?, String?) -> Void)) {
        let session = URLSession.shared
        let url = URL(string: APIClientConstants.authenticateURL)!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        let body = [
            APIClientConstants.refreshTokenBodyKey: refreshToken,
            APIClientConstants.clientIdBodyKey: APIClientConstants.clientIdBodyValue,
            APIClientConstants.grantTypeBodyKey: APIClientConstants.grantTypeBodyValue
        ]
        request.httpBody = buildBody(body)
        let task = session.dataTask(with: request) { data, response, error in
            if let data = data,
                let responseDict = try? JSONSerialization.jsonObject(with: data, options: []) as? [String : Any],
                let accessToken = responseDict[APIClientConstants.accessTokenResponseParameter] as? String,
                let refreshToken = responseDict[APIClientConstants.refreshTokenResponseParameter] as? String {
                completion(accessToken, refreshToken)
            }else {
                completion(nil, nil)
            }
        }
        
        task.resume()
    }
    
    private func buildBody(_ params: [String: String]) -> Data? {
        var components = URLComponents()
        components.queryItems = params.map { (key, value) in URLQueryItem(name: key, value: value) }
        let bodyString = components.percentEncodedQuery
        return bodyString?.data(using: .utf8)
    }
}
