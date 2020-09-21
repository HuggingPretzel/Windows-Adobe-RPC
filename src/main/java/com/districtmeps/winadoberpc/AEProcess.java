  
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

import java.util.regex.Pattern;

public class AEProcess {

    private String imageName;
    private Long PID;
    private String status;
    private String cputTime;
    private String windowTitle;

    public AEProcess(String imageName, Long PID, String status, String cpuTime, String windowTitle){
        this.imageName = imageName;
        this.PID = PID;
        this.status = status;
        this.cputTime = cpuTime;
        this.windowTitle = windowTitle;
    }

    public AEProcess(String[] info){
        this.imageName = info[0].substring(1);
        this.PID = Long.parseLong(info[1]);
        this.status = info[5];
        this.cputTime = info[7];
        this.windowTitle = info[8].substring(0, info[8].length()-1);
    }

    public String getName(){
        return imageName;
    }

    public Long getPID(){
        return PID;
    }

    public String getStatus(){
        return status;
    }

    public String getTime(){
        return cputTime;
    }

    public String getTitle(){
        return windowTitle;
    }

    public String returnAll(){
        return getName() + "\n" + getPID() + "\n" + getStatus() + "\n" + getTime() + "\n" + getTitle();
    }
    
    public String[] seperateWindowTitle(){
        if(status.equals("Unknown")){
            return null;
        } else {
            String[] title = windowTitle.split(" - ");

            try{
                if (title[1].contains("\\")){
                    String[] uri = title[1].split(Pattern.compile("\\\\").toString());
                    title[1] = uri[uri.length - 1];
                }
                return title;

            } catch (ArrayIndexOutOfBoundsException e){
                return new String[]{"Updating...", "Updating..."};
            }
            
        }


        
    }

    public boolean isEqual(AEProcess ae){
        return ae.getName().equals(imageName) &&
        ae.getPID().equals(PID) &&
        ae.getStatus().equals(status) &&
        ae.getTitle().equals(windowTitle);
    }
}
