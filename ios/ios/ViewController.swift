//
//  ViewController.swift
//  ios
//
//  Created by Nathaniel on 2/18/17.
//  Copyright © 2017 newsin.pictures. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        //get the JSON response with all of the news URLs from the API
        let session = URLSession(configuration: URLSessionConfiguration.default)
        
        let request = NSURLRequest(url: NSURL(string: "https://thousand-words.appspot.com/getnews.json")! as URL)
        
        
        let task: URLSessionDataTask = session.dataTask(with: request as URLRequest) { (data, response, error) -> Void in
            if let data = data {
                let response = NSString(data: data, encoding: String.Encoding.utf8.rawValue)
                
                //print(response!)
                
                //var d = jsonString.data(using: .utf8)!
                
                do {
                    if let json = try JSONSerialization.jsonObject(with: data, options:.allowFragments) as? [String:Any] {
                        if let nytimes = json["the-new-york-times"] {
                            if let
                        }
                    }
                } catch let err{
                    print(err.localizedDescription)
                }


            }
        }
        task.resume()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

