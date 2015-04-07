# speech-recognition-server

This is a project to host CMUSphinx on AWS Elastic Beanstalk via a Jersey REST api. Its goal is to power saypenis.com.

To run this, make sure you run on hardware with sufficient memory. When run on a t1.micro with default memory settings, the Elastic Beanstalk timeout will kill connections and you won't get any responses.

Sample url: http://recognition-service.elasticbeanstalk.com/api/v1/s3recognize?s3bucket=mjd-srs&s3file=test.wav
