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

train = featuresTrain.copy()
test = featuresTest.copy()

def rf_holdout():
    # Random Forest com Holdout
    rf_train_input = train.drop(columns=['CP','CP -1','CP -2','CP -3','CP -4','CP -5','R -1','R -2','R -3','R -4','R -5','R'])  # Features
    rf_train_output = rf_train_input.pop('B') # Target

    rf_test_input = test.drop(columns=['CP','CP -1','CP -2','CP -3','CP -4','CP -5','R -1','R -2','R -3','R -4','R -5','R'])  # Features
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
    
    print("Acurácia do Modelo:", accuracy_model)
    print("Acurácia do Baseline:", accuracy_baseline)


def rf_janela_deslizante(num_days):
    jd_train_input = train.drop(columns=['CP','CP -1','CP -2','CP -3','CP -4','CP -5','R -1','R -2','R -3','R -4','R -5','R'])  # Features
    jd_train_output = jd_train_input.pop('B') # Target

    jd_test_input = test.drop(columns=['CP','CP -1','CP -2','CP -3','CP -4','CP -5','R -1','R -2','R -3','R -4','R -5','R'])  # Features
    jd_test_output = jd_test_input.pop('B') # Target
    
    limite = 1

    new_train_input = jd_train_input
    new_train_output = jd_train_output

    date = jd_test_input['Date'].values

    accuracys = []
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
            accuracys.append(accuracy_score(new_target, predict))
    
        new_train_input = pd.concat([new_train_input, new_features])
        new_train_output = pd.concat([new_train_output, new_target])
        
    predicts = [elemento for sublista in predicts for elemento in sublista]
    for i in range(len(predicts)):
        out.append(f"{date[i]}, {predicts[i]}")

    print(out)


def teste():
    teste = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1 ,1] 

    date = test['Date'].values


    print(len(teste))

    out = []
    for i in range(len(date)):
        out.append([date[i], teste[i]])

    print(out)

#rf_holdout()
rf_janela_deslizante(5) 
## Rede Neural - MLP
