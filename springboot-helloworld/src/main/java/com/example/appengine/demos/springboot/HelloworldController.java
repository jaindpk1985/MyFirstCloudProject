/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine.demos.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.*;

@RestController
public class HelloworldController {
  @GetMapping("/")
  public String hello() {
    return "Hello world - Deepak Jain came to Cloud";
  }
  
  @CrossOrigin("http://bestatone.com")
  @PostMapping(value = "/saveNumber")
  @ResponseBody
	public String saveNumber(@RequestParam String number, @RequestParam String pinCode) {
		Statement stmt = Configuration.getStatementFromDB();
		try {
			String insertQuery = "insert into UserNotificationPref values(null,'" + number + "','" + pinCode+ "',null,null)";
			stmt.execute(insertQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error";
		}
		return "Success";
	}
}
