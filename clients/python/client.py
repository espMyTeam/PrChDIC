#! /usr/bin/python
#-*- coding:utf-8 -*-

import requests
import sseclient
import json

class ChatUser:
	def __init__(self, pseudo):
		self._title = "Logiciel de discussion en ligne"
		self._pseudo = pseudo
		self._idChatRoom = None
		self._URI_AIGUILLEURS = []
		self._webTarget = None
		

	def parameter(self):
		self._URI_AIGUILLEURS.append("http://localhost:8080/ChatRoomAiguilleurOne/aiguillage/AiguilleurOne")
		self._URI_AIGUILLEURS.append("http://localhost:8080/ChatRoomAiguilleurTwo/aiguillage/AiguilleurTwo")


	def actionSubscribe(self, idChatRoom):
		""" integrer un chatRoom"""
		if self._webTarget != None:
			self.put(self._webTarget+"/subscribe", {'idChatRoom': idChatRoom})

	def actionUnsubscribe(self, idChatRoom):
		""" quitter un chatRoom"""
		if self._webTarget != None:
			self.put(self._webTarget+"/unsubscribe", {'idChatRoom': idChatRoom})

	def actionPostMessage(self):
		"""Envoyer un message"""
		if self._webTarget != None:
			self.post(self._webTarget+"/post", {'idChatRoom': idChatRoom})

	def actionCreateChatRoom(self):
		""" creer un chatRoom"""
		if self._webTarget != None:
			self.put(self._webTarget+"/create", {'idChatRoom': idChatRoom})


	def removeChatRoom(self):
		""" ssupprimer un chatRoom"""
		if self._webTarget != None:
			self.put(self._webTarget+"/delete", {'idChatRoom': idChatRoom})

	def actionGetAllChatRoom(self):
		"""recuperer tous les salons de discussion"""
		if self._webTarget != None:
			res = self.get(self._webTarget+"/getallchatroom")

	def actionGetUsers(self):
		"""recuperer tous les salons de discussion"""
		if self._webTarget != None:
			res = self.get(self._webTarget+"/getusers")


	def test(self):
		"""Parcourt la liste des aiguilleurs en testant la disponibilite de chaque aiguilleur, pour choisir la premiere disponible"""
		test_OK = False
		while test_OK == False:
			for uri in self._URI_AIGUILLEUR:
				uri_path = uri + "/test"
				res = self.get(uri_path)
				if res != None && res.text=="UP:OK":
					self._webTarget = uri
					test_OK = True
					break




	def get(self, url, params=None):
		""" """
		try:
			res = requests.get(url, params=params)
			if res.status_code == 200:
				return res
			else:
				return None
		except:
			return None

	def post(self, url, params):
		try:
			requests.post(url, data=params)
		except:
			return None

	def put(self, url, params=[]):
		""" """
		try:
			res = requests.put(url, data=params)

		except:
			return None

		
	
