package be.uantwerpen.idlab.cobra.blockgen.tools.loopboundanalysis.analysers;

import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

public abstract class AnnotationParser
{
    public abstract Block parseLoopbounds(Block block) throws Exception;
}
