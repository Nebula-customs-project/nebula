import PropTypes from "prop-types";

/**
 * PropTypes for the Vehicle3DScene component.
 */
export const vehicle3DScenePropTypes = {
  vehicleName: PropTypes.string,
  configuration: PropTypes.shape({
    paint: PropTypes.string,
  }).isRequired,
  modelPath: PropTypes.string.isRequired,
  onModelLoad: PropTypes.func,
};

export const vehicle3DSceneDefaultProps = {
  vehicleName: "",
  onModelLoad: () => {},
};
