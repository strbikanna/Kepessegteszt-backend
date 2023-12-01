import statsmodels.api as sm
import math
import tensorflow as tf
from sklearn.preprocessing import StandardScaler
import numpy as np
import joblib
import os


def normalize_array(array):
    return [1 / (1 + math.exp(-elem)) for elem in array]

def aranyosit(array):
    sum_ = sum(array)
    return [elem / sum_ for elem in array]

def reszkepesseg_fugges(reszkepessegek, valaszok):
    X = sm.add_constant(reszkepessegek)
    model = sm.OLS(valaszok, X)
    result = model.fit()
    coeffs = normalize_array(result.params)
    coeffs = coeffs[1:]
    return coeffs

def reszkepesseg_fugges_aranyok(reszkepessegek, valaszok):
    return aranyosit(reszkepesseg_fugges(reszkepessegek, valaszok))

def eredmenybecsloModellTanitas(reszkepessegek, valaszok, path):
    X = np.array(reszkepessegek)
    Y = np.array(valaszok)

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    # Hálózat létrehozása
    model = tf.keras.Sequential([
        tf.keras.layers.Dense(16, activation='relu'),
        tf.keras.layers.Dense(16, activation='relu'),
        tf.keras.layers.Dense(1)  # Nincs aktivációs függvény a kimeneti rétegen
    ])

    # Hálózat összeállítása
    model.compile(loss='mean_squared_error', optimizer='adam')

    # Hálózat tanítása
    model.fit(X_scaled, Y, epochs=100, batch_size=64, verbose=0)

    if not os.path.exists(path):
        os.makedirs(path)

    joblib.dump(scaler, f'{path}/scaler.joblib')
    model.save(f'{path}/model.keras')

    return path

def eredmenybecsles(path, reszkepessegek):
    model = tf.keras.models.load_model(f'{path}/model.keras')
    scaler = joblib.load(f'{path}/scaler.joblib')
    X = np.array([reszkepessegek])
    X = scaler.transform(X)
    Y = model.predict(X, verbose=0)
    return Y
