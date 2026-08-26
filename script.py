firmenname = "Ecosia GmbH"
print("Hallo liebes Team von " + firmenname + ", ich möchte gerne mit euch in der Firma " + firmenname + " arbeiten. Ich würde mich über eine Antwort freuen.")



aufgaben = ["Sport", "Notebook lernen", "Essen kochen","Wäsche waschen", "lesen"]
print("Heute stehen diese Aufgaben an:")

for i, task in enumerate(aufgaben, 1):
    print(str(i) + ". " + task)