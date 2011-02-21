package de.xzise.xwarp;

import java.util.List;

import me.taylorkelly.mywarp.Warp;

public class MatchList {
	public MatchList(List<Warp> exactMatches, List<Warp> matches) {
		this.exactMatches = exactMatches;
		this.matches = matches;
	}

	public List<Warp> exactMatches;
	public List<Warp> matches;
}
