name = input("Wie heißt du?")
alter = input("Wie alt bist du?")
zusatzjahre = input("In wieviel Jahren willst du dein Alter wissen?")
print("Hallo", name)
print("In ",zusatzjahre + "Jahren bist du ", int(alter) + int(zusatzjahre))
## hier war ein fehler, er sieht es als string statt int. Hab es gecastet in int(variable)

