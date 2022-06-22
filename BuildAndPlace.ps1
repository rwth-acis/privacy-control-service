gradle clean build

if (Test-Path ..\las2peer\node-storage\) {
    Remove-Item ..\las2peer\node-storage\ -r
}

Copy-Item .\privacy_control_service\export\jars\i5.las2peer.services.privacyControlService-0.1.0.jar ..\las2peer\core\lib\