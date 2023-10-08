import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

# Lendo os arquivo csv gerado pelo meu simulador 
# Excluindo todas as linhas do meu DataFrame que não possuem valores
dataBase = pd.read_csv('./datas/Data.csv')  
dataBase = dataBase.dropna()

# Dividir base em treinamento e teste
featuresTrain = dataBase[dataBase['Date'] < 20220000]
featuresTest = dataBase[dataBase['Date'] > 20220000]

# HoldOut
## Random Forest

rf_train = featuresTrain.copy()
rf_test = featuresTest.copy()

rf_train_input = rf_train.drop(columns=['CP','CP -1','CP -2','CP -3','CP -4','CP -5','R -1','R -2','R -3','R -4','R -5','R'])  # Features
rf_train_output = rf_train_input.pop('B') # Target

rf_test_input = rf_test.drop(columns=['CP','CP -1','CP -2','CP -3','CP -4','CP -5','R -1','R -2','R -3','R -4','R -5','R'])  # Features
rf_test_output = rf_test_input.pop('B') # Target

RandomForest = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=42)
RandomForest.fit(rf_train_input, rf_train_output.values)
    
predict = RandomForest.predict(rf_test_input)
accuracy = accuracy_score(rf_test_output, predict)
print("Acurácia:", accuracy * 100)















## Rede Neural - MLP
mlp_train = featuresTrain.copy()
mlp_test = featuresTest.copy()

mlp_train_input = mlp_train.drop('B', axis=1)  # Features
mlp_train_output = mlp_train_input.pop('R') # Target

mlp_test_input = mlp_test.drop('B', axis=1)  # Features
mlp_test_output = mlp_test_input.pop('R') # Target

