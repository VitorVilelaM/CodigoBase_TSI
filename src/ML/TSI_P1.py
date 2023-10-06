import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report

# Lendo os arquivo csv gerado pelo meu simulador 
# Excluindo todas as linhas do meu DataFrame que não possuem valores

dataBase = pd.read_csv('./datas/Data.csv')
dataBase = dataBase.dropna()

# Selecionar entrada/saida do Random Forest e da MLP

inpRF = dataBase.copy()
del inpRF['Regression']
outRF = inpRF.pop('Binary')

inpMLP = dataBase.copy()
del inpMLP['Binary']
outMLP = inpMLP.pop('Regression')

# Holdout
 ## Random Forest

featuresTrain = inpRF[inpRF['Date'] < 20220000]
featuresTest = inpRF.iloc[featuresTrain.shape[0]:inpRF.shape[0]+1, :]

aux = outRF.array
outTrain = aux[0: featuresTrain.shape[0]]
outTest = aux[featuresTrain.shape[0] : outRF.shape[0]]

rf_model = RandomForestClassifier(random_state=1)
rf_model.fit(featuresTrain, outTrain)

print(featuresTrain)

y_pred = rf_model.predict(featuresTest)
accuracy = accuracy_score(outTest, y_pred)

## print("Acurácia:", accuracy)
## print(classification_report(outTest, y_pred))


