from parameter import Parameter
from leaf_node import LeafNode
from concurrent.futures import ThreadPoolExecutor
from concurrent.futures import as_completed

class InternalNode:
    """Represents the internal nodes of the threaded tree.
    
    Responsible for generating child nodes (Internal/Leaf) by varying 
    the parameter next in the hierarchy.

    Attributes:
        depth_in_tree: An integer for the depth of the node in the tree.
        parameter_to_value: A dictionary mapping parameter names to values for all 
                            parameters higher or equal in hierarchy w.r.t depth_in_tree.
    """

    def __init__(self, depth_in_tree, parameter_to_value):
        """Initializes InternalNode with depth_in_tree and parameter_to_value."""

        self.depth_in_tree = depth_in_tree
        self.parameter_to_value = parameter_to_value.copy()

    def __create_child_node_objects(self, is_in_last_internal_node_row, next_parameter_info):
        """Performs creation of child nodes (Internal/Leaf).

        Generates a list of LeafNode child nodes if the current node is in the last row of 
        internal nodes of the tree, represented by is_in_last_internal_node_row = False. 
        Generates a list of InternalNode child nodes otherwise.

        Parameter To Value for each child node contains an additional parameter: 
        NEXT_PARAMETER [the parameter which is next in the hierarchy].Adjacent child 
        nodes differ in the value of the NEXT_PARAMETER by an amount = STEP_SIZE
        [of the NEXT_PARAMETER].

        Args:
            is_in_last_internal_node_row: A boolean indicating whether the current node 
                                        occupies the last row of internal nodes in the tree.
            next_parameter_info: A dictionary mapping info of the next parameter next in hierarchy
                                to their values. This includes:
                                    <ol>
                                    <li> Name </li>
                                    <li> Minimum Value </li>
                                    <li> Maximum Value </li>
                                    <li> Range of values </li>
                                    <li> Step Size </li>
                                    </ol>

        Returns:
            A list of newly created child nodes (Internal/Leaf).
        """

        child_nodes = []
        child_node_parameter_to_value = self.parameter_to_value.copy()
        
        for next_parameter_value in range(next_parameter_info["min"], next_parameter_info["max"] + 1, next_parameter_info["step_size"]): 
            child_node_parameter_to_value[next_parameter_info["name"]] = next_parameter_value

            if is_in_last_internal_node_row:
                child_nodes.append(LeafNode(child_node_parameter_to_value))
            else:
                child_nodes.append(InternalNode(self.depth_in_tree + 1, child_node_parameter_to_value))

        return child_nodes, len(child_nodes)

    def __get_next_parameter_info(self):
        """Retrieves information of the parameter next in the hierarchy w.r.t. the current node.

        The parameter next in the hierarchy is the parameter at index = depth_in_tree 
        in the list HIERARCHY of the parameter.Parameter class. 

        Returns:
            A dictionary mapping info of the next parameter next in hierarchy
            to their values. This includes:
                <ol>
                <li> Name </li>
                <li> Minimum Value </li>
                <li> Maximum Value </li>
                <li> Range of values </li>
                <li> Step Size </li>
                </ol>
        """

        next_parameter_info = {}
        info = [
            "name", 
            "min", 
            "max", 
            "range", 
            "step_size"
            ]

        for _info in info:
            if _info == "name":
                next_parameter_info[_info] = Parameter.HIERARCHY[self.depth_in_tree]
            elif _info == "min":
                if next_parameter_info["name"] == "std_dev_length_of_text_with_file":
                    next_parameter_info[_info] = min(
                                                    self.parameter_to_value["mean_length_of_text_with_file"], 
                                                    Parameter.EXTREMA[next_parameter_info["name"]]["min"]
                                                    )
                elif next_parameter_info["name"] == "std_dev_file_size":
                    next_parameter_info[_info] = min(
                                                    self.parameter_to_value["mean_file_size"] - 1,
                                                    Parameter.EXTREMA[next_parameter_info["name"]]["min"]
                                                    )
                else:
                    next_parameter_info[_info] = Parameter.EXTREMA[next_parameter_info["name"]]["min"]
            elif _info == "max":
                if next_parameter_info["name"] == "std_dev_length_of_text":
                    next_parameter_info[_info] = min(
                                                    self.parameter_to_value["mean_length_of_text"] - 1, 
                                                    Parameter.EXTREMA[next_parameter_info["name"]]["max"]
                                                    )
                elif next_parameter_info["name"] == "std_dev_length_of_text_with_file":
                    next_parameter_info[_info] = min(
                                                    self.parameter_to_value["mean_length_of_text_with_file"], 
                                                    Parameter.EXTREMA[next_parameter_info["name"]]["max"]
                                                    )
                elif next_parameter_info["name"] == "std_dev_file_size":
                    next_parameter_info[_info] = min(
                                                    self.parameter_to_value["mean_file_size"] - 1, 
                                                    25 - self.parameter_to_value["mean_file_size"],
                                                    Parameter.EXTREMA[next_parameter_info["name"]]["max"]
                                                    )
                else:
                    next_parameter_info[_info] = Parameter.EXTREMA[next_parameter_info["name"]]["max"]
            elif _info == "range":
                next_parameter_info[_info] = next_parameter_info["max"] - next_parameter_info["min"]
            else:
                next_parameter_info[_info] = Parameter.STEP_SIZE[next_parameter_info["name"]]
        
        # Sets the range to the nearest multiple of its step size
        if next_parameter_info["range"] % next_parameter_info["step_size"]:
            next_parameter_info["max"] += next_parameter_info["step_size"] - next_parameter_info["range"] % next_parameter_info["step_size"]
            next_parameter_info["range"] = next_parameter_info["max"] - next_parameter_info["min"]

        return next_parameter_info

    def __get_is_in_last_internal_row_node(self):
        """Checks if the current node should produce Internal or Leaf child nodes.

        Returns:
            True: If the current node should product Leaf child nodes i.e. if 
                depth_of_tree = total number of parameters to vary - 1.
            False: Otherwise. 
        """
        return self.depth_in_tree == Parameter.COUNT - 1

    def create_child_nodes(self):
        """Creates child thread nodes of current node."""

        is_in_last_internal_node_row = self.__get_is_in_last_internal_row_node()
        next_parameter_info = self.__get_next_parameter_info()
        child_nodes, child_node_count = self.__create_child_node_objects(is_in_last_internal_node_row, next_parameter_info)
        results = []
        
        with ThreadPoolExecutor(max_workers = child_node_count) as executor:
            if is_in_last_internal_node_row:
                leaf_node_results = list(executor.map(LeafNode.call_generate_message_content_dataset, child_nodes))

                return leaf_node_results
            
            internal_node_results = list(executor.map(InternalNode.call_create_child_nodes, child_nodes))

            internal_node_results_flattened = []

            for internal_node_result in internal_node_results:
                for leaf_node_result in internal_node_result:
                    internal_node_results_flattened.append(leaf_node_result)

            return internal_node_results_flattened

    @staticmethod
    def call_create_child_nodes(internal_node):
        """Calls the instance method create_child_nodes."""
        return internal_node.create_child_nodes()
