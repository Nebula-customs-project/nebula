import PropTypes from "prop-types";

/**
 * PropTypes for the CustomizationPanel component.
 */
export const customizationPanelPropTypes = {
  categories: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      name: PropTypes.string.isRequired,
      icon: PropTypes.string,
      parts: PropTypes.arrayOf(
        PropTypes.shape({
          id: PropTypes.string.isRequired,
          name: PropTypes.string.isRequired,
          visualKey: PropTypes.string.isRequired,
          cost: PropTypes.number.isRequired,
          description: PropTypes.string,
        }),
      ).isRequired,
    }),
  ).isRequired,
  activeCategory: PropTypes.string.isRequired,
  setActiveCategory: PropTypes.func.isRequired,
  configuration: PropTypes.object.isRequired,
  onPartSelect: PropTypes.func.isRequired,
};
