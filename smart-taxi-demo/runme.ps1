# global variables
$currentDir = "C:/Projects/JDEECo/smart-taxi-demo/"
$mainClass = "cz.cuni.mff.d3s.deeco.demo.SmartCarSharing"
$userDir = "C:\Users\Ondrej"
$classPath = "C:\Projects\JDEECo\smart-taxi-demo\target\classes;C:\Projects\JDEECo\jdeeco-grouper-plugin\target\classes;C:\Projects\JDEECo\jdeeco-core\target\classes;C:\Projects\JDEECo\jdeeco-core\target\test-classes;$userDir\.m2\repository\xml-apis\xml-apis\1.3.04\xml-apis-1.3.04.jar;$userDir\.m2\repository\junit\junit\4.11\junit-4.11.jar;$userDir\.m2\repository\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;$userDir\.m2\repository\org\mockito\mockito-all\1.9.5\mockito-all-1.9.5.jar;$userDir\.m2\repository\com\github\stefanbirkner\system-rules\1.6.0\system-rules-1.6.0.jar;$userDir\.m2\repository\commons-io\commons-io\2.4\commons-io-2.4.jar;$userDir\.m2\repository\junit-addons\junit-addons\1.4\junit-addons-1.4.jar;$userDir\.m2\repository\xerces\xercesImpl\2.6.2\xercesImpl-2.6.2.jar;$userDir\.m2\repository\xerces\xmlParserAPIs\2.6.2\xmlParserAPIs-2.6.2.jar;$userDir\.m2\repository\org\eclipse\emf\org.eclipse.emf.ecore\2.9.0-v20130528-0742\org.eclipse.emf.ecore-2.9.0-v20130528-0742.jar;$userDir\.m2\repository\org\eclipse\emf\org.eclipse.emf.common\2.9.0-v20130528-0742\org.eclipse.emf.common-2.9.0-v20130528-0742.jar;$userDir\.m2\repository\org\eclipse\emf\org.eclipse.emf.ecore.xmi\2.9.0-v20130528-0742\org.eclipse.emf.ecore.xmi-2.9.0-v20130528-0742.jar;$userDir\.m2\repository\uk\com\robust-it\cloning\1.9.0\cloning-1.9.0.jar;$userDir\.m2\repository\org\objenesis\objenesis\1.2\objenesis-1.2.jar;$userDir\.m2\repository\org\bouncycastle\bcprov-jdk15\1.46\bcprov-jdk15-1.46.jar;C:\Projects\JDEECo\jdeeco-gossip-plugin\target\classes;C:\Projects\JDEECo\jdeeco-network-plugin\target\classes;C:\Projects\JDEECo\jdeeco-omnet-plugin\target\classes;C:\Projects\JDEECo\jdeeco-simulation-omnet-native\target\classes;$userDir\.m2\repository\junit\junit\4.12\junit-4.12.jar;C:\Projects\JDEECo\jdeeco-matsim-plugin\target\classes;$userDir\.m2\repository\org\matsim\matsim\0.5.0\matsim-0.5.0.jar;$userDir\.m2\repository\log4j\log4j\1.2.17\log4j-1.2.17.jar;$userDir\.m2\repository\org\jfree\jfreechart\1.0.19\jfreechart-1.0.19.jar;$userDir\.m2\repository\org\jfree\jcommon\1.0.23\jcommon-1.0.23.jar"
$logDir = "E:/tmp/logs/"
#"$($currentDir)logs/tmp/"
$matsimConfig = "$($currentDir)config/matsim/berlin/config.xml"
$maxThreads = 3

$template = "$($currentDir)config/template.properties"
$duration = 900000   # 15 minutes
$knPer = 5000 # 5 second

function WaitForEnd
{
    Write-Host "Waiting for jobs to be finished " -NoNewline
    $count = 0
    while (@(Get-Job).Count -gt 0) {
        Get-Job -State Completed | Remove-Job
        Get-Job -State Failed | Remove-Job
        Get-Job -State Stopped | Remove-Job
        Start-Sleep -Seconds 10
        Write-Host "." -NoNewline
        $count++
        if ($count -gt 40) {
            $count = 0
            Write-Host ":"
        }
    }
}
function InitEnv
{
    cd $currentDir
    Get-Job -State Stopped | Remove-Job

    # clear previous logs
    Remove-Item "$logDir\*"
}
function CreateConfig ($id, $hdPeriod, $plPeriod, $knPeriod, $probability, $publishCount, $grpCount, $localTimeout, $globalTimeout, $features, $ensembles, $devices, $duration)
{
    $configFile = "$($logDir)config$($id).properties"
    $logFile = "$($logDir)log$($id).csv"

    (Get-Content $template) | ForEach-Object {
        $_ -replace '\$\{hdPeriod\}', $hdPeriod `
        -replace '\$\{plPeriod\}', $plPeriod `
        -replace '\$\{knPeriod\}', $knPeriod `
        -replace '\$\{probability\}', $probability `
        -replace '\$\{publishCount\}', $publishCount `
        -replace '\$\{localTimeout\}', $localTimeout `
        -replace '\$\{globalTimeout\}', $globalTimeout `
        -replace '\$\{logFile\}', $logFile `
        -replace '\$\{features\}', $features `
        -replace '\$\{duration\}', $duration `
        -replace '\$\{ensembles\}', $ensembles `
        -replace '\$\{devices\}', $devices `
        -replace '\$\{grouperCount\}', $grpCount `
        -replace '\$\{run\}', $id `
        -replace '\$\{currentDir\}', $currentDir
    } | Set-Content $configFile
}
function RunJob($id)
{
    $configFile = "$($logDir)config$($id).properties"

    $job = Start-Job -ScriptBlock { 
        
        $id = $args[0]
        $configFile = $args[1]
        $mainClass = $args[2]
        $classPath = $args[3]
        $currentDir = $args[4]
        $logDir = [System.IO.Path]::GetDirectoryName($configFile)

        cd $currentDir
        c:\ProgramFiles\java\jdk8_x86\bin\java.exe '-classpath' $classPath $mainClass $configFile 2>&1 > "$logDir/out$($id).out"

    } -ArgumentList $id, $configFile, $mainClass, $classPath, $currentDir
    Register-ObjectEvent -InputObject $job -EventName StateChanged -Action { 
        $eventSubscriber | Unregister-Event
        $eventSubscriber.Action | Remove-Job
        
    } | Out-Null
}
function RunConfig($id, $hdPeriod, $plPeriod, $knPeriod, $probability, $publishCount, $grpCount, $localTimeout, $globalTimeout, $features, $ensembles, $devices, $duration)
{
    Write-Host "Creating config $id ... " -NoNewline
    CreateConfig $id $hdPeriod $plPeriod $knPeriod $probability $publishCount $grpCount $localTimeout $globalTimeout $features $ensembles $devices $duration
    Write-Host "done!"
    
    if (@(Get-Job).Count -ge $maxThreads) {
        Write-Host "Waiting for running jobs " -NoNewline
    }
    $count = 0
    while (@(Get-Job -State Running).Count -ge $maxThreads) {
        Get-Job -State Completed | Remove-Job
        Start-Sleep -Seconds 10
        Write-Host "." -NoNewline
        $count++
        if ($count -gt 40) {
            $count = 0
            Write-Host ":"
        }
    }

    Write-Host ""
    Write-Host "Running job $id ..."
    RunJob $id $logDir
}
function RunSimulation
{
    $id = 0;
    $featureSet = ('logger;push;pull') #, 'logger;push')

    foreach ($features in $featureSet)
    {
        for ($t = 1; $t -le 4; $t++)             # timeout
        {
            for ($h = 0; $h -le 3; $h++)         # HD period
            {
                for ($p = 0; $p -le 3; $p++)     # PL period
                {
                    for ($i = 0; $i -le 3; $i++) # probability
                    {
                        # KN <= HD <= PL
                        # KN <= LT <= GT

                        $knPer = 5000       # constant
                        $hdPer = $knPer + 2000 * $h
                        $plPer = $hdPer + 2000 * $p
                    
                        $prob = 0.3 * $i

                        $locTime = $knPer + 2000 * $t
                        $globTime = $locTime + 2000 * $t
                        $ensembles = "cz.cuni.mff.d3s.deeco.demo.ensemble.SimplePositionAggregator"
                        $pubCount = 0
                        $grpCount = 0
    
                        RunConfig $id $hdPer $plPer $knPer $prob $pubCount $grpCount $locTime $globTime $features $ensembles $devices $duration
                        $id = $id + 1
                    }
                }
            }
        }
    }
}
function MANETSimple($id)
{
    for ($i = 0; $i -le 10; $i++) # probability
    {
        $prob = 0.1 * $i
        $knPer = 5000
        $features = ('logger;push')
        $ensembles = "cz.cuni.mff.d3s.deeco.demo.ensemble.SimplePositionAggregator"
        $devices = "omnet-broadcast"


        CreateConfig $id 0 0 $knPer $prob 0 0 0 0 $features $ensembles $devices $duration

        $id = $id + 1
    }
}
function MANETBoundary($id)
{
    for ($i = 0; $i -le 10; $i++) # probability
    {
        $prob = 0.1 * $i
        $knPer = 5000
        $features = ('logger;push')
        $ensembles = "cz.cuni.mff.d3s.deeco.demo.ensemble.BoundaryPositionAggregator"
        $devices = "omnet-broadcast"

        CreateConfig $id 0 0 $knPer $prob 0 0 0 0 $features $ensembles $devices $duration

        $id = $id + 1
    }
}
function IPSimple($id)
{
    for ($i = 1; $i -le 3; $i++)
    {
        for ($j = 0; $j -le 5; $j++)
        {
            $pubCount = $i # number of known nodes
            $prob = 0.1 * $j
            $features = ('logger;push')
            $ensembles = "cz.cuni.mff.d3s.deeco.demo.ensemble.SimplePositionAggregator"
            $devices = "omnet-infrastructure"
            $globTime = $knPer * 3
            $grpCount = 0

            CreateConfig $id 0 0 $knPer $prob $pubCount $grpCount 0 $globTime $features $ensembles $devices $duration

            $id = $id + 1
        }
    }
}
function IPGrouper($id)
{
    for ($i = 1; $i -le 3; $i++)
    {
        for ($j = 0; $j -le 5; $j++)
        {
            $pubCount = $i
            $grpCount = 8 # number of groupers
            $prob = 0.1 * $j
            $features = ('logger;push;grouper')
            $ensembles = "cz.cuni.mff.d3s.deeco.demo.ensemble.SimplePositionAggregator"
            $devices = "omnet-infrastructure"
            $globTime = $knPer * 3

            CreateConfig $id 0 0 $knPer $prob $pubCount $grpCount 0 $globTime $features $ensembles $devices $duration

            $id = $id + 1
        }
    }
}
function MANETPull($id)
{
    for ($t = 1; $t -le 4; $t++)             # timeout
    {
        for ($h = 0; $h -le 3; $h++)         # HD period
        {
            for ($p = 0; $p -le 3; $p++)     # PL period
            {
                for ($i = 0; $i -le 3; $i++) # probability
                {
                    # KN <= HD <= PL
                    # KN <= LT <= GT

                    $hdPer = $knPer + 2000 * $h
                    $plPer = $hdPer + 2000 * $p
                    
                    $prob = 0.3 * $i
                    $locTime = $knPer + 2000 * $t
                    $globTime = $locTime + 2000 * $t

                    $ensembles = "cz.cuni.mff.d3s.deeco.demo.ensemble.SimplePositionAggregator"
                    $pubCount = 0
                    $grpCount = 0
                    $features = ('logger;push;pull')
                    $devices = "omnet-broadcast"
    
                    CreateConfig $id $hdPer $plPer $knPer $prob $pubCount $grpCount $locTime $globTime $features $ensembles $devices $duration
                    $id = $id + 1
                }
            }
        }
    }
}
function RunConfigs
{
    foreach($file in (Get-ChildItem $logDir -Filter "*.properties"))
    {
        $id = $file.Name.Replace("config", "").Replace(".properties", "")
        
        if (@(Get-Job).Count -ge $maxThreads) {
            Write-Host "Waiting for running jobs " -NoNewline
        }
        $count = 0
        while (@(Get-Job -State Running).Count -ge $maxThreads) {
            Get-Job -State Completed | Remove-Job
            Start-Sleep -Seconds 10
            Write-Host "." -NoNewline
            $count++
            if ($count -gt 40) {
                $count = 0
                Write-Host ":"
            }
        }

        Write-Host ""
        Write-Host "Running job $id ..."
        RunJob $id
    }
}

# prepare the script for running
#InitEnv

#MANETSimple(1)
#MANETBoundary(12)
#IPSimple(23)
#IPGrouper(40)
#MANETPull(58)

RunConfigs

# wait for all jobs to be finished
WaitForEnd