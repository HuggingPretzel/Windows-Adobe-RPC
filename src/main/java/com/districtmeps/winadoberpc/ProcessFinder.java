  
/**
 *      Copyright 2020 Daniel Sanchez
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package com.districtmeps.winadoberpc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessFinder {
    
    public static AEProcess getAeProcess(){
        try {

            List<String> output = new ArrayList<>();
            Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /v /fi \"IMAGENAME eq AfterFX.exe\"");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            output.add(input.readLine());
            if (output.get(0) != null && !output.get(0).equals("INFO: No tasks are running which match the specified criteria.")) {
                String line;
                while ((line = input.readLine()) != null) {
                    output.add(line);
                }
        
                AEProcess aeProcess = null;
        
                if (output.size() > 2) {
        
                List<AEProcess> aeMulti = new ArrayList<>();
                for(int i = 1; i < output.size(); i++){
                    aeMulti.add(new AEProcess(output.get(i).split("\",\"")));
                }
                
                for(int i = 0; i < aeMulti.size(); i++){
                    if(aeMulti.get(i).getStatus().equals("Running")){
                    aeProcess = aeMulti.get(i);
                    i = aeMulti.size();
                    }
                }
                } else {
                aeProcess = new AEProcess(output.get(1).split("\",\""));
                }
        
                
                if(aeProcess.getStatus().equals("Running")){
        
                return aeProcess;
                } else {
                return null;
                }
      
            } 
            input.close();
            return null;
          } catch (Exception err) {
            err.printStackTrace();
            return null;
          }
    }
}
