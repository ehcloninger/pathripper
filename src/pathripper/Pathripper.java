/*
Copyright 2014 Eric H. Cloninger, dba PurpleFoto
 
Licensed under the Apache License, Version 2.0 (the "License"); you 
may not use this file except in compliance with the License. You may 
obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
implied. See the License for the specific language governing 
permissions and limitations under the License
 */

package pathripper;

import java.io.File;

/*
 * Pathripper looks for directories in the current %PATH% or $PATH environment variable.
 * If the directory doesn't exist, it is flagged. If the directory exists but doesn't contain
 * any executable programs, it is flagged. The result is a suggested PATH based on existence
 * and contents.
 */
public class Pathripper {
	public static void main(String[] args) {
		
		// Start by trying to discover if $OSTYPE value is set, suggesting Linux 
		// or Windows running a shell (e.g. Cygwin or minGW) 
		String os_type_val = System.getenv("OSTYPE");
		if (os_type_val == null)
		{
			os_type_val = System.getenv("OS");
		}
		
		String pathSep =":";
		
		if (os_type_val.equals("Windows_NT"))
		{
			pathSep =";";
		}
		else
			if (os_type_val.equals("msys") || 
				os_type_val.equals("cygwin") || 
				os_type_val.equals("linux-gnu") || 
				os_type_val.startsWith("darwin"))
			{
				pathSep =":";
			}
			else
			{
				System.out.println("Can't determine HOST OS type.\n");
				return;
			}
        
		String regex = "[" + pathSep +"]+";
		
		// Pull the PATH from the environment
        String oldPath = System.getenv("PATH");
        String newPath = "";
        
        // Break path into parts, based on the separator
        String[] dirs = oldPath.split(regex);
        
        // Iterate through each entry
        for (int i = 0; i < dirs.length; i++)
        {
        	File f = new File(dirs[i]);
        	
        	// Does the entry exist?
        	if (f.exists()) 
        	{
        		// Is it a directory?
        		if (f.isDirectory())
        		{
        			// Now, is there anything actually in the directory?
        		    File[] files = new File(f.getAbsolutePath()).listFiles();
        		    boolean anyExecutable = false;
        		    for (File file : files) {
        		        if (!file.isDirectory()) 
        		        {
        		        	// Looks like we have a winner
        		        	if (file.canExecute())
        		        	{
        		        		anyExecutable = true;
        		        		if (newPath.isEmpty())
        		        			newPath = dirs[i];
        		        		else
        		        			newPath += pathSep + dirs[i];
        		        		break;
        		        	}
        		        }
        		    }
        		    
        		    // Let the user know the directory exists, but there's nothing of value there
        		    if (!anyExecutable)
                		System.out.printf("Entry [%s] is a directory, but has no executable files\n", dirs[i]);
        		}
        		
        		// Not a directory, it was a file (who would do this?)
        		else
        		{
            		System.out.printf("Entry [%s] is a file, not a directory\n", dirs[i]);
        		}
        	}
        	
        	// Directory does not exist
        	else
        	{
        		System.out.printf("Entry [%s] does not exist\n", dirs[i]);
        	}
       	}
        
        // Put out old and new for the user to see
        System.out.printf("Old PATH\n%s\n\n", oldPath);
        System.out.printf("New PATH\n%s\n", newPath);
	}
}
 