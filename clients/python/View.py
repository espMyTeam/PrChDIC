#! /usr/bint/python
#-*- coding:utf-8 -*-

import Tkinter

class ChatUserImpl(Tkinter.Tk):
	def __init__(self):
		Tkinter.Tk.__init__(self)
		self.initialise()

	def initialise(self):
		""" initilisaer la fenetre """
		self.geometry("700x600")
		self.title("ChatUser")

		#menu
		menubar = Tkinter.Menu(self)

		#fichier
		fichier = Tkinter.Menu(menubar, tearoff=0)
		fichier.add_command(label="Statut")
		fichier.add_command(label="Profil")
		fichier.add_command(label="Démarrer une nouvelle conversation")
		fichier.add_command(label="Deconnexion")
		fichier.add_command(label="Quitter", command=self.quit)
		menubar.add_cascade(label="Fichier", menu=fichier)

		# salons
		salon = Tkinter.Menu(menubar, tearoff=0)
		salon.add_command(label="Nouvelle salon")
		salon.add_command(label="Liste des salons")
		menubar.add_cascade(label="Salon de discussion", menu=salon)

		# outils
		outil = Tkinter.Menu(menubar, tearoff=0)
		outil.add_command(label="Paramètres")
		menubar.add_cascade(label="Outil", menu=outil)


		#aide
		aide = Tkinter.Menu(menubar, tearoff=0)
		aide.add_command(label="En savoir plus")
		aide.add_command(label="A propos")
		menubar.add_cascade(label="Aide", menu=aide)

		self.config(menu=menubar)


	def NouvelleSalon(self):
		""" creation de nouvelle salon"""

	def ListeSalons(self):
		"""liste de toutes les salons dont l'utilisateur fait parties"""

	def demarrerConversation(self):
		"""demarrer une nouvelle conversation"""
		pannelUsers = Frame()
		pannelMessages = Frame()
		pannelSaisieMessage = Frame()

	




		


	

