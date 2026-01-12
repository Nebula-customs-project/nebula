/**
 * Available Car Models
 * 
 * List of all available 3D car models in the public/models directory.
 * Each car has a display name and model path.
 */

export const availableCars = [
  {
    id: 'furarri',
    name: 'Furari',
    modelPath: '/models/furarri.glb',
  },
  {
    id: 'gtr',
    name: 'GTR',
    modelPath: '/models/GTR.glb',
  },
  {
    id: 'deluxo',
    name: 'Deluxo',
    modelPath: '/models/Deluxo.glb',
  },
  {
    id: 'deviant',
    name: 'Deviant',
    modelPath: '/models/Deviant.glb',
  },
  {
    id: 'gauntlet',
    name: 'Gauntlet',
    modelPath: '/models/Gauntlet.glb',
  },
  {
    id: 'impaler',
    name: 'Impaler',
    modelPath: '/models/Impaler.glb',
  },
  {
    id: 'infernus',
    name: 'Infernus',
    modelPath: '/models/Infernus.glb',
  },
  {
    id: 'dacia',
    name: 'Dacia',
    modelPath: '/models/Dacia.glb',
  },
  {
    id: 'p911',
    name: 'P-911',
    modelPath: '/models/P-911.glb',
  },
  {
    id: 'roadstar',
    name: 'Roadstar',
    modelPath: '/models/Roadstar.glb',
  },
  {
    id: 'nfs',
    name: 'NFS Car',
    modelPath: '/models/NFS_car.glb',
  },
  {
    id: 'tarzan',
    name: 'Tarzan Wonder',
    modelPath: '/models/TarzanWonderCar.glb',
  },
]

export const getCarById = (id) => {
  return availableCars.find((car) => car.id === id) || availableCars[0]
}

export const getCarIndex = (id) => {
  return availableCars.findIndex((car) => car.id === id)
}
