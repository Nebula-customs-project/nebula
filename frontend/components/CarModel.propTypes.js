import PropTypes from "prop-types";
import * as THREE from "three";

/**
 * PropTypes and default props for the CarModel component.
 * Separated for cleaner component code.
 */
export const carModelPropTypes = {
  modelPath: PropTypes.string,
  configuration: PropTypes.object,
  paintMaterial: PropTypes.shape({
    color: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.string,
      PropTypes.number,
    ]).isRequired,
    metalness: PropTypes.number,
    roughness: PropTypes.number,
  }),
  onError: PropTypes.func,
  onLoad: PropTypes.func,
};

export const carModelDefaultProps = {
  modelPath: "/models/furarri.glb",
  configuration: {},
  paintMaterial: {
    color: new THREE.Color(0xff0000),
    metalness: 0.5,
    roughness: 0.5,
  },
  onError: () => {},
  onLoad: () => {},
};
