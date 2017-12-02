# Glacier multipart uploader

A simple command line application for uploading files to AWS
Glacier.

[![Build Status](https://travis-ci.org/kosbr/glacier-multipart-uploader.svg?branch=master)](https://travis-ci.org/kosbr/glacier-multipart-uploader)

## Features

* Multipart uploading (1 MB size of a part)
* Proceeding upload if it was interrupted (useful for big files)
* AWS Glacier configuration storing 

## Requirements

### System requirements 
* linux operation system
* java 8 installed

### Configuration requirements

The application needs prepared command line access to AWS 
Glacier. It can be reached by creating IAM user with proper
permissions (for example 'AmazonGlacierFullAccess') and 
configuring AWS command line client. See the official AWS
documentation to get more detailed info. As a result, the
following file should exist ~/.aws/credentials.

If you have access to your vaults from AWS command line, it 
is enough for this applications.

## Download and launch

Download jar file from the last release: 
https://github.com/kosbr/glacier-multipart-uploader/releases/latest

Then launch it:

```
java -jar aws-uploader.jar
```

Use console commands that are described below for using
application.

## Available commands

#### configure

It creates a configuration that will be used in further uploads.
The configuration consists of its name, singing region and
 service endpoint. The handler of this command asks these
 parameters in dialog mode and saves new configuration.
 
 Example: 
```
configure
```
 
#### config-list
It prints all configurations that have been created.

Example:
```
config-list
```

#### config-use

Before starting an upload, it is needed to clarify what
configuration should be used for it. This command does it.
Use '--name' option for configuration reference. 

Example
```
config-use --name myconfig
```

#### upload

Starts multipart upload. It needs three options:

* vault: The name of the existing vault.
* description: Short description.
* archive: Absolute path to the uploaded file.

Example:
```
upload --vault Archives --description smallfile --archive /home/kosbr/file.zip
```

During upload it displays a progress in percents.

#### uploads-list

Displays all interrupted uploads.

If the upload has been interrupted because of something, for example
 manual exit or IOException, the information about 
 this upload will be saved. This command displays all such
 uploads with their ids.

Example:
```
uploads-list
```

#### upload-proceed

It proceeds the interrupted upload. It is needed to give
an Id for interrupted upload reference (Get it from the
uploads-list command).

Example:
```
upload-proceed --upload-id 122
```

#### config-delete

Removes existing configuration. If there are any 
uploads exist, which use this configuration, the user
will be warned about deleting them (not files, just 
internal information about it). 

Example:
```
config-delete --name myconfig
```

#### config-reset
Removes all configurations and all interrupted uploads 
(not files, just internal information about it).
In other words, it clears all internal memory of the 
application.

Example:
```
config-reset
```

#### exit

Exit from the application.

Example:
```
exit
```


