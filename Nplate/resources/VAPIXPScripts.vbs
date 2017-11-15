'===========================================================================================================================
'Function Name : getParamValue
'Description   :  Get the parameter value of the VAPI-XP script
'Arguments     : paramName,CurrentTSTest
'Return value  : paramValue - Value of the parameter
'============================================================================================================================

Function getParamValue(paramName,CurrentTSTest)
blnTrue = "TRUE"
     'Output the parameters
      Set TSParams = CurrentTSTest.Params
       For i = 0 To TSParams.Count -1
           If StrComp(Trim(TSParams.ParamName(i)), Trim(paramName), 1) = 0 Then
             blnTrue = "TRUE"
             paramValue = TSParams.ParamValue(i)
           End If
       Next
       If blnTrue ="TRUE" Then
         If paramValue = "" Then
           Set paramValueFct = CurrentTSTest.ParameterValueFactory
           Set lst = paramValueFct.NewList("")
           For Each param In lst
           With param
              If StrComp(Trim(.Name), Trim(paramName), 1) = 0 Then
                blnTrue = "TRUE"
                paramhtmlValue = .DefaultValue
                htmlValues = split(paramhtmlValue,"<span")
                htmlValue1 = split(htmlValues(1),"</span>")
                htmlValue2 = Split(htmlValue1(0),">")
                paramValue = htmlValue2(1)
              End If
           End With
           Next
         End If
       End If
       getParamValue = paramValue
End Function

'===========================================================================================================================
'Function Name : readFile
'Description   :  To read the status from the text file
'Arguments     : sPath
'Return value  : sData - Status of the test from the text file
'============================================================================================================================

function readFile(sPath)
    const forReading = 1
    dim objFSO, objFile, sData, sContent
    set objFSO = createobject("Scripting.FileSystemObject")
    TDOutput.Print sPath            
    If (objFSO.FileExists(sPath)) Then
                
                                set objFile = objFSO.openTextFile(sPath, ForReading)
								sContent = objFile.ReadAll()
                                sData = Trim(sContent)
							'	sData = "Passed"
                             '   sContent = objFile.ReadAll()

                              '  If instr(sContent,"Failed") > 0 then

                               '                 sData = "Failed" 

                                'End If
                                objFile.close
                                set objFile = nothing
                                set objFSO = nothing
                Else
                                sData = "Not Completed"
                End If
                'msgbox "status is "&sData
    readFile = sData            
end function

'===========================================================================================================================
'Function Name : getResultfile
'Description   :  To get the path of the result folder
'Arguments     : resultfolder, sTCName
'Return value  : oNewFold.Path - Path of the result
'============================================================================================================================

function getResultfile(resultfolder, sTCName)
      getResultfile = ""
      Set objFSO = CreateObject("Scripting.FileSystemObject")
      FolderToScan = resultfolder
      Set objFolder = objFSO.GetFolder(FolderToScan)
      Set oNewFold = Nothing
      NewestDate = #1/1/1970#

      For Each objFold In objFolder.SubFolders
          If objFold.DateLastModified > NewestDate Then
              NewestDate = objFold.DateLastModified
              Set oNewFold = objFold
          End If
      Next
      Set objFold = Nothing
      If Not oNewFold Is Nothing Then
           getResultfile =  oNewFold.Path
      End If
      Set objFSO = nothing
end function

'===========================================================================================================================
'Function Name : getZipFileName
'Description   : To get the zipped report File
'Arguments     : resultfolder, sTCName
'Return value  : oNewFold.Path - Path of the result
'============================================================================================================================

function getZipFileName(resultfolder, sTCName)
      getZipFileName = ""
      Set objFSO = CreateObject("Scripting.FileSystemObject")
      FolderToScan = resultfolder
      Set objFolder = Nothing
      Set objFolder = objFSO.GetFolder(FolderToScan)
      Set oNewFold = Nothing
      NewestDate = #1/1/1970#

      For Each objFold In objFolder.SubFolders
          If objFold.DateLastModified > NewestDate Then
              NewestDate = objFold.DateLastModified
              Set oNewFold = objFold
          End If
      Next
      getZipFileName= oNewFold.name
end function

'===========================================================================================================================
'Function Name : executeScriptandAttachResult
'Description   : To trigger the test method from the command prompt and Attach the result to ALM after the execution
'Arguments     : Debug,CurrentTSTest,CurrentRun,TDHelper
'Return value  : ouresult - Status of the test script
'============================================================================================================================

Function executeScriptandAttachResult(Debug, CurrentTestSet,CurrentTSTest, CurrentRun, TDHelper, projectDir) 
  Dim testSuitName, sTCName,objWSH, objUserVariables,strProjectPath,objFSO, outFile, objShell, resultfolder, resultfile,getZipName, ouresult,strENVIRONMENT,strBROWSER
  'sTCName = replace(Trim(CurrentTSTest.TestName),"$","#")
  sTCName = replace(Trim(CurrentTSTest.TestName),"$","#")
 ' msgbox(sTCName)
'Create a batch file
	resultfolder = projectDir & "\Results"
  Set objFSO=CreateObject("Scripting.FileSystemObject")
  outFile = projectDir & "\resources\autorun.bat"
  'TDOutput.Print outFile
 ' msgbox(outFile) c:\Opensource\apache-maven-3.3.9\bin\
  Set objFile = objFSO.CreateTextFile(outFile,True)
  objFile.Write "hostname" & vbCrlf
  objFile.Write "call mvn -version" & vbCrLf
  objFile.Write "cd " & projectDir & "" & vbCrLf  
  objFile.Write "call mvn -Dtest="& sTCName & " test -DExecuteFromHPALM=Y -l " & resultfolder & "\Executionlog.txt" &  vbCrLf 
  'objFile.Write "pause"  & vbCrLf
  objFile.Close
  XTools.Sleep 1000
  Set objFile = NOTHING

'trigger the test script from command prompt
  Set objShell = CreateObject("WScript.Shell")
  on error resume next
                'msgbox strProjectPath
  objShell.Run "cmd /c " & outFile , 1, True
  XTools.Sleep 1000
  
  
   'get the html reprot file name
  ouresult = readFile(resultfolder & "\"& sTCName & ".txt" )
  TDOutput.Print  ouresult
  Set objShell = NOTHING
 ' msgbox ouresult
  strFilePath = resultfolder & "\" & sTCName & ".zip"
  strFilePath1 = resultfolder & "\Executionlog.txt"
  'msgbox strFilePath
 ' PublishCommandLineOutput(CurrentRun)
 call QC_AttachFileToTestRun ( CurrentRun, strFilePath )
 'call QC_AttachFileToTestRun ( CurrentRun, strFilePath1 )
  executeScriptandAttachResult = ouresult
End Function


'#######################################################################################################################
'Function Description   : Function to publish the command line output into the test run results 
'Input Parameters       : CurrentRun
'Return Value           : None
'Author                 : Cognizant
'Date Created           : 26/12/2012
'#######################################################################################################################
Sub PublishCommandLineOutput(CurrentRun)
	Dim objStep: Set objStep =  CurrentRun.StepFactory.AddItem(Null)
	objStep.Name = "Execution Log"
	objStep.Status = "N/A"
	objStep.Field("ST_DESCRIPTION") = "Execution Log attached"
	objStep.Post
	
	Set objStep = Nothing
End Sub

Function QC_AttachFileToTestRun( CurrentRun , strFilePath )
    Dim objQCAttachments, objQCAttachments_Item
    Set objQCAttachments =  CurrentRun.Attachments
    Set objQCAttachments_Item = objQCAttachments.AddItem(Null)
    objQCAttachments_Item.FileName = strFilePath
    objQCAttachments_Item.Type = 1
    objQCAttachments_Item.Post
    Set objQCAttachments = Nothing
    Set objQCAttachments_Item = Nothing
End Function
'#######################################################################################################################