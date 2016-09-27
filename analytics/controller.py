from __future__ import print_function # In python 2.7
from flask import Flask
from flask import request
import sys
import os
import ssl
import speech_recognition as sr
import subprocess

app = Flask(__name__)
port = int(os.getenv('VCAP_APP_PORT', 8080))


@app.route('/audio/<url>/<bearer>')
def audio(url, bearer):
	print("AUDIO WAS CALLED", file=sys.stderr)
	import requests

	headers = {
		'Authorization':bearer
		,'Predix-Zone-Id': 'f512233c-3401-4a4c-bfee-863504f410ab'
	}

	req = requests.get('https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/'+url, headers = headers)

		
	subprocess.call('rm audio.mp3', shell=True)
	with open('audio.mp3','wb') as output:
		output.write(req.content)

	subprocess.call('ffmpeg-normalize -f audio.mp3', shell=True)

	# r = sr.Recognizer()
	# with sr.WavFile("normalized-audio.wav") as source:
	#     audio = r.record(source) # read the entire audio file
	audio = open("normalized-audio.wav",'rb')
	url = "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize"
	username= "46470518-6c6f-459b-ad7f-512cea103cf9"
	password= "j7qVfMLQtTur"

	response = requests.post(url, auth=(username, password), headers={"Content-Type": "audio/wav"},data=audio)

	return response.text

	# recognize speech using IBM Speech to Text
	# IBM_USERNAME = "46470518-6c6f-459b-ad7f-512cea103cf9" # IBM Speech to Text usernames are strings of the form XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
	# IBM_PASSWORD = "j7qVfMLQtTur" # IBM Speech to Text passwords are mixed-case alphanumeric strings
	# try:
	#     return("IBM Speech to Text thinks you said " + r.recognize_ibm(audio, username=IBM_USERNAME, password=IBM_PASSWORD))
	# except sr.UnknownValueError:
	#     return("IBM Speech to Text could not understand audio")
	# except sr.RequestError as e:
	#     return("Could not request results from IBM Speech to Text service; {0}".format(e))

@app.route('/shellcommand', methods=["GET","POST"])
def comd():
	subprocess.call(request.data, shell=True)
	return "Executed"

@app.route('/video/<url>/<bearer>')
def video(url, bearer):
	import requests

	headers = {
		'Authorization':bearer
		,'Predix-Zone-Id': 'f512233c-3401-4a4c-bfee-863504f410ab'
	}

	req = requests.get('https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/'+url, headers = headers)
	print("\n\n________________________________________________")
	subprocess.call('rm video.mp4', shell=True)
	with open('video.mp4','wb') as output:
		output.write(req.content)

	# import pylab
	import imageio
	filename ='video.mp4'
	vid = imageio.get_reader(filename,  'ffmpeg')
	nums = [10, 287]
	for image in vid.iter_data():
	    print(image)
	# return vid.iter_data()[0]
	    # pylab.imshow(image)
	# pylab.show()

	# print "VIDEO " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url
	return "VIDEO " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url

@app.route('/image/<url>/<bearer>')
def image(url, bearer):
	# print "IMAGE " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url
	return "IMAGE " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=port)
