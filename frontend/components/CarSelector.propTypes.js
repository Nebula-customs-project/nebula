import PropTypes from "prop-types";

/**
 * PropTypes and default props for the CarSelector component.
 */
export const carSelectorPropTypes = {
  currentCarId: PropTypes.string.isRequired,
  onCarChange: PropTypes.func.isRequired,
  availableCars: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      name: PropTypes.string.isRequired,
      modelPath: PropTypes.string,
    }),
  ).isRequired,
};