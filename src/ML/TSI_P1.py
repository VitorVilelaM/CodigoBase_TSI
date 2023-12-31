import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.metrics import roc_auc_score
from sklearn.metrics import recall_score
from sklearn.metrics import precision_score
from sklearn.neural_network import MLPRegressor
from sklearn.metrics import mean_absolute_error
from sklearn.metrics import mean_squared_error
import numpy as np

import h2o
from h2o.estimators import H2ODeepLearningEstimator

# Utilizar h2o ou sklearn para mlp

# Lendo os arquivo csv gerado pelo meu simulador 
# Excluindo todas as linhas do meu DataFrame que não possuem valores
dataBase = pd.read_csv('./datas/Data.csv')  
dataBase = dataBase.dropna()

# Dividir base em treinamento e teste
featuresTrain = dataBase[dataBase['Date'] < 20200000]
featuresTest = dataBase[dataBase['Date'] > 20200000]

train = featuresTrain.copy()
test = featuresTest.copy()

date = featuresTest['Date'].values
result = featuresTest['B'].values

outOtimo = []

for i in range(len(date)):
    outOtimo.append([date[i],0])

## Random Forest
def rf_holdout():
    # Random Forest com Holdout
    rf_train_input = train.drop(columns=['Date','R'])  # Features
    rf_train_output = rf_train_input.pop('B') # Target

    # 107,52 -> Melhor
    # 68,94 -> O meu
    # 5,02 -> Baseline

    date =  test['Date'].values

    rf_test_input = test.drop(columns=['Date','R'])  # Features
    rf_test_output = rf_test_input.pop('B') # Target

    # Criando o meu Baseline
    baseline = []
    for i in range(0, rf_test_output.shape[0]):
        baseline.append(0)

    RandomForest = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=42)
    RandomForest.fit(rf_train_input, rf_train_output.values)
        
    predict = RandomForest.predict(rf_test_input)

    accuracy_model = accuracy_score(rf_test_output, predict)
    accuracy_baseline = accuracy_score(baseline, predict)
    auc_model = roc_auc_score(rf_test_output, predict)
    precision_model = precision_score(rf_test_output, predict)
    recall_model = recall_score(rf_test_output, predict)

    print("Acurácia do Modelo:", accuracy_model)
    print("Acurácia do Baseline:", accuracy_baseline)
    print("AUC:", auc_model)
    print("Recall:", recall_model)
    print("Precision:", precision_model)

    print()
    out = []

    for i in range(len(predict)):
        out.append([date[i],predict[i]])

    df = pd.DataFrame(out)
    caminho_arquivo = './output.csv'
    df.to_csv(caminho_arquivo, index=False)

def rf_janela_deslizante(num_days):
    # Random Forest com Janela Deslizante  
    jd_train_input = train.drop(columns=['Date','R'])  # Features
    jd_train_output = jd_train_input.pop('B') # Target

    jd_test_input = test.drop(columns=['R'])  # Features
    jd_test_output = jd_test_input.pop('B') # Target
    
    limite = jd_test_input.shape[0]

    new_train_input = jd_train_input
    new_train_output = jd_train_output

    date = jd_test_input['Date'].values
    jd_test_input = jd_test_input.drop(columns='Date')

    copy_test_output = jd_test_output.copy()

    predicts = []
    out = []

    while( limite > 0):
        limite = jd_test_input.shape[0]

        if(num_days > limite):
            num_days = limite
       
        new_features = jd_test_input.iloc[0:num_days]
        jd_test_input = jd_test_input.drop(jd_test_input.index[0:num_days])    

        new_target = jd_test_output.iloc[0:num_days]
        jd_test_output = jd_test_output.drop(jd_test_output.index[0:num_days])    
        
        if(limite > 0):
            RandomForest = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=42)
            RandomForest.fit(new_train_input, new_train_output.values)
            
            predict = RandomForest.predict(new_features)
            predicts.append(predict)
    
        new_train_input = pd.concat([new_train_input, new_features])
        new_train_output = pd.concat([new_train_output, new_target])
        
    predicts = [elemento for sublista in predicts for elemento in sublista]
    for i in range(len(predicts)):
        out.append(f"{date[i]}, {predicts[i]}")

    accuracy_model = accuracy_score(copy_test_output, predicts)
    auc_model = roc_auc_score(copy_test_output, predicts)
    precision_model = precision_score(copy_test_output, predicts)
    recall_model = recall_score(copy_test_output, predicts)

    print("Acurácia do Modelo:", accuracy_model)
    print("AUC:", auc_model)
    print("Recall:", recall_model)
    print("Precision:", precision_model)

    print()

    print(out)

rf_holdout()
#rf_janela_deslizante(22) 


## Rede Neural - MLP

def mlp_holdout():
    h2o.init()

    mlp_train_input = train.drop(columns=['Date','B'])  # Features
    mlp_train_output = mlp_train_input.pop('R') # Target

    data = h2o.import_file('./test.csv')

    mlp_test_input = test.drop(columns=['Date','B'])  # Features
    mlp_test_output = mlp_test_input.pop('R') # Target
    
    learning_rate = 0.01

    model = H2ODeepLearningEstimator(hidden=[50, 50], epochs=10)
    model.train(training_frame=mlp_train_input, y=mlp_train_output)
    print(data)

    h2o.shutdown()

def mlp_janela_deslizante(num_days):
    mlp_train_input = train.drop(columns=['Date','B'])  # Features
    mlp_train_output = mlp_train_input.pop('R') # Target

    mlp_test_input = test.drop(columns=['Date','B'])  # Features
    mlp_test_output = mlp_test_input.pop('R') # Target

    baseline = [0]
    for i in range(1, mlp_test_output.shape[0]):
        baseline.append(mlp_test_output.values[i - 1])

    #print(baseline)

    
    # 106,82 -> Melhor
    # 106,76 -> O meu
    # 15,24 -> Baseline

    mlp = MLPRegressor(hidden_layer_sizes=(10, 5), # número de camadas ocultas e quantidade de neurônios
                    max_iter=5000, # número máximo de iterações
                    learning_rate_init=0.1, # taxa de aprendizado inicial
                    solver="lbfgs", # estratégia: algoritmo da descida do gradiente estocástico
                    activation="tanh", # função de ativação (sigmóide)
                    learning_rate="constant", # taxa constante
                    )
    
    # Treine o modelo
    mlp.fit(mlp_train_input,mlp_train_output)    
    predict = mlp.predict(mlp_test_input)

    out = []
    date = test['Date'].values
    for i in range(len(predict)):
        out.append([date[i], mlp_test_output.values[i]])

    df = pd.DataFrame(out)
    caminho_arquivo = './output.csv'
    df.to_csv(caminho_arquivo, index=False)


    mae = mean_absolute_error(mlp_test_output,predict)
    mse = mean_squared_error(mlp_test_output,predict)
    rmse = np.sqrt(mse)

    print(f"Valor de MAE: {mae}")
    print(f"Valor de RMSE: {rmse}")

#mlp_holdout()
#mlp_janela_deslizante(5)