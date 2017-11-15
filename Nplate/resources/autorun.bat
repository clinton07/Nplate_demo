hostname
call mvn -version
cd C:\Watchdox\ServiceCloud_Maven_Craft
call mvn -Dtest=MDL.Data_Entry.DE_TC05_EXTuser_Create_Therapy_Initaition_SR#DE_TC_05 test -DExecuteFromHPALM=Y -l C:\Watchdox\ServiceCloud_Maven_Craft\Results\Executionlog.txt
