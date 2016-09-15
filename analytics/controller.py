"""Cloud Foundry test"""
from flask import Flask
from flask import request
import os
import speech_recognition as sr
import subprocess

app = Flask(__name__)
port = int(os.getenv('VCAP_APP_PORT', 8080))


@app.route('/audio/<url>/<bearer>')
def audio(url, bearer):
	import requests

	headers = {
		'Authorization':bearer
		,'Predix-Zone-Id': 'f512233c-3401-4a4c-bfee-863504f410ab'
	}

	req = requests.get('https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/'+url, headers = headers)
	print "________________________________________________"
	# with open('audio.mp3','wb') as output:
	# 	output.write(req.content)


	# subprocess.call('ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Linuxbrew/install/master/install)";PATH="$HOME/.linuxbrew/bin:$PATH";echo \'export PATH="$HOME/.linuxbrew/bin:$PATH"\' >>~/.bash_profile;brew install ffmpeg', shell=True)
	# subprocess.call('echo $PATH', shell=True)
	# subprocess.call('\'export PATH="$HOME/.linuxbrew/bin:$PATH"\' >>~/.bash_profile', shell=True)
	# subprocess.call('echo $PATH', shell=True)
	# subprocess.call(['ffmpeg-normalize', '-v audio.mp3'])
	# print "Normalized"

	# r = sr.Recognizer()
	# with sr.AudioFile('normalized-audio.wav') as source:
	# 	print "_____"
	# 	audio = r.record(source) 

	# print r.recognize_sphinx(audio)
	print "AUDIO   " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url

	return "AUDIO   " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url

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
	print "________________________________________________"
	with open('video.mp4','wb') as output:
		output.write(req.content)

	# import pylab
	import imageio
	filename = 'video.mp4'
	vid = imageio.get_reader(filename,  'ffmpeg')
	nums = [10, 287]
	for image in vid.iter_data():
	    print image
	    # pylab.imshow(image)
	# pylab.show()

	print "VIDEO " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url
	return "VIDEO " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url

@app.route('/image/<url>/<bearer>')
def image(url, bearer):
	print "IMAGE " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url
	return "IMAGE " + "https://ie-media-service.run.aws-usw02-pr.ice.predix.io/media/file/" + url

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=port)
