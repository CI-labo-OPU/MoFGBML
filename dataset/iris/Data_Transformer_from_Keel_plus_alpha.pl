print "Input data name (e.g., glass, crx, iris)\n";
$dataname = <STDIN>;
chomp($dataname);
#$dataname = "crx";

############################
### Check data charators ###
############################

$filename = "$dataname"."-10-1tra.dat";
open(IN, "$filename");
@file = <IN>;
close(IN);

for($i=0;$i<@file;$i++){ # remove return code
  $file[$i] =~ s/[\r\n]//; # windows
  $file[$i] =~ s/[\r]//;   # mac
  $file[$i] =~ s/[\n]//;   # linux
}

# check inputs and outputs
for($i=0;$i<@file;$i++){
  $file[$i] =~ s/, /,/g; # remove a space after comma

  if($file[$i] =~ /\@inputs/){
    print "$file[$i]\n";
    # extract attribute names
    ($gomi, $att_names) = split(/ /, $file[$i]);
    @att_name = split(/,/,$att_names);
    for($j=0;$j<@att_name;$j++){
      $att_kind{"$att_name[$j]"} = "other";
    }
  }
  if($file[$i] =~ /\@outputs/){
    ($gomi, $output) = split(/ /,$file[$i]);
    print "$output\n";
  }
  if($file[$i] =~ /\@data/){
    last;
  }
}

# check kind of  attributes and outputs
for($i=0;$i<@file;$i++){
  if($file[$i] =~ /\@attribute/){
    print "$file[$i]\n";
    @temp = split(/ /,$file[$i]);

    # for attributes
    if(exists($att_kind{"$temp[1]"})){
      if($temp[2] eq "integer" || $temp[2] eq "real"){
	$att_kind{"$temp[1]"} = "$temp[2]";
      }else{
	$temp[2] =~ s/{|}//g;
	$att_other{"$temp[1]"} = $temp[2];
	@other_temp = split(/,/,$temp[2]);
	$att_min{"$temp[1]"} = 0;
	$n_temp = @other_temp - 1;
	$att_max{"$temp[1]"} = $n_temp;
      }
    }
    
    # for class
    if($file[$i] =~ /$output/){
      if($file[$i] =~ /{.+}/){
	$class_temp = $&;
	$class_temp =~ s/{|}//g;
	@class_name = split(/,/,$class_temp);
	for($j=0;$j<@class_name;$j++){
	  print "$class_name[$j], ";
	  $class_number{"$class_name[$j]"} = $j;
	}
	print "\n";
      }
    }
  }
  if($file[$i] =~ /\@data/){
    $data_line = $i + 1;
    last;
  }
}

for($i=0;$i<@att_name;$i++){
  $att_kind_temp = $att_kind{"$att_name[$i]"};
  print "$att_name[$i]: $att_kind_temp\n";
}


################################
### check class distribution ###
################################

$filename = "$dataname"."-10-1tst.dat";
open(IN, "$filename");
@file_test = <IN>;
close(IN);

for($i<0;$i<@file_test;$i++){ # remove return code
  $file_test[$i] =~ s/[\r\n]//; # windows
  $file_test[$i] =~ s/[\r]//;   # mac
  $file_test[$i] =~ s/[\n]//;   # linux
}

for($i=$data_line;$i<@file;$i++){
  $data[$i - $data_line] = $file[$i];
}

$end_line = @data;
for($i=$data_line;$i<@file_test;$i++){
  $data[$end_line + $i - $data_line] = $file_test[$i];
}

for($i=0;$i<@data;$i++){
  $data[$i] =~ s/, /,/g; # remove a space after comma
  # print "$data[$i]\n";
}
$n_ptt_total = @data;
# print "$n\n";

$n_class = @class_name;

for($i=0;$i<$n_class;$i++){
  $n_patterns_per_class[$i] = 0;
}

for($i=0;$i<@data;$i++){
  @temp = split(/,/,$data[$i]);
  $temp_value = $temp[$#temp];
  $n_patterns_per_class[$class_number{"$temp_value"}] ++;
}

for($i=0;$i<@n_patterns_per_class;$i++){
  print "Class $class_name[$i]: $n_patterns_per_class[$i], ";
}
print "\n";
print "Modification...\n";
$num_temp = 0;
for($i=0;$i<@class_name;$i++){
  if($n_patterns_per_class[$i] > 0){
    $class_number{"$class_name[$i]"} = $num_temp;
    print "Class $class_name[$i] => $num_temp\n";
    $num_temp ++;
  }
}
$n_class = $num_temp;


######################################
### Check max and min values       ###
### for interger & real attributes ###
######################################

for($i=0;$i<@data;$i++){
  if($data[$i] !~ /<null>/ && $data[$i] !~ /\?/){
    @temp = split(/,/,$data[$i]);
    for($k=0;$k<@temp - 1;$k++){
      if($att_kind{"$att_name[$k]"} eq "real" ||
	 $att_kind{"$att_name[$k]"} eq "integer"){
	$att_min{"$att_name[$k]"} = $temp[$k];
	$att_max{"$att_name[$k]"} = $temp[$k];
      }
    }
    print "$i\n";
    last;
  }
}

$n_pattern_with_missing = 0;

for($i=0;$i<@data;$i++){
  if($data[$i] !~ /<null>/ && $data[$i] !~ /\?/){
    @temp = split(/,/,$data[$i]);
    for($k=0;$k<@temp - 1;$k++){
      if($att_kind{"$att_name[$k]"} eq "real" ||
	 $att_kind{"$att_name[$k]"} eq "integer"){
	if($att_min{"$att_name[$k]"} > $temp[$k]){
	  $att_min{"$att_name[$k]"} = $temp[$k];
	}
	if($att_max{"$att_name[$k]"} < $temp[$k]){
	  $att_max{"$att_name[$k]"} = $temp[$k];
	}
      }
    }
  }else{
    $n_pattern_with_missing ++;
  }
}

print "\n";
print "Show attribute information\n";
for($i=0;$i<@att_name;$i++){
  $kind = $att_kind{"$att_name[$i]"};
  $minv = $att_min{"$att_name[$i]"};
  $maxv = $att_max{"$att_name[$i]"};
  print "$att_name[$i]: $kind\t Min=$minv, Max=$maxv";
  if($kind eq "other"){
    $cate = $att_other{"$att_name[$i]"};
    print " {$cate}\n";
  }else{
    print " \n";
  }
}
print "Show class information\n";
for($i=0;$i<@n_patterns_per_class;$i++){
  print "Class $class_name[$i]: $n_patterns_per_class[$i], ";
}
print "\n";
for($i=0;$i<@class_name;$i++){
  if($n_patterns_per_class[$i] > 0){
    $num_temp = $class_number{"$class_name[$i]"};
    print "Class '$num_temp' represents '$class_name[$i]'\n";
  }
}
$n_att = @att_name;
print "# of patterns: $n_ptt_total, # of attributes: $n_att, # of classes: $n_class\n";
print "# of patterns with missing attribute(s): $n_pattern_with_missing\n";
print "\n";

open(OUT_Info, "> data_info.txt");
for($i=0;$i<@att_name;$i++){
  $kind = $att_kind{"$att_name[$i]"};
  $minv = $att_min{"$att_name[$i]"};
  $maxv = $att_max{"$att_name[$i]"};
  print OUT_Info "$att_name[$i]: $kind\t Min=$minv, Max=$maxv";
  if($kind eq "other"){
    $cate = $att_other{"$att_name[$i]"};
    print OUT_Info " {$cate}\n";
  }else{
    print OUT_Info " \n";
  }
}
for($i=0;$i<@n_patterns_per_class;$i++){
  print OUT_Info "Class $class_name[$i]: $n_patterns_per_class[$i], ";
}
print OUT_Info "\n";
for($i=0;$i<@class_name;$i++){
  if($n_patterns_per_class[$i] > 0){
    $num_temp = $class_number{"$class_name[$i]"};
    print OUT_Info "Class '$num_temp' represents '$class_name[$i]'\n";
  }
}
print OUT_Info "# of patterns: $n_ptt_total, # of attributes: $n_att, # of classes: $n_class\n";
print OUT_Info "# of patterns with missing attribute(s): $n_pattern_with_missing\n";
print OUT_Info "\n";

close(OUT_Info);

########################################
### Change data set in Nojima format ###
########################################
@filenum1 = ('1','2','3','4','5','6','7','8','9','10');
@filenum2 = ('tra','tst');

for($i=0;$i<10;$i++){
  for($j=0;$j<2;$j++){

    # open an original file
    $filename = "$dataname"."-10-"."$filenum1[$i]"."$filenum2[$j]".".dat";
    open(IN, "$filename");
    @data = ();
    @data = <IN>;
    close(IN);

    for($k=0;$k<@data;$k++){ # remove return code
      $data[$k] =~ s/[\r\n]//; # windows
      $data[$k] =~ s/[\r]//;   # mac
      $data[$k] =~ s/[\n]//;   # linux
      $data[$k] =~ s/, /,/g; # remove a space after comma
      if($data[$k] =~ /\@data/){
	$start_line = $k + 1;
      }
    }

    for($k=0;$k<$n_class;$k++){
      $npc[$k] = 0;
    }

    # remove discription and lines with <null>
    $n_temp = 0;
    @m_data = ();
    for($k=$start_line;$k<@data;$k++){
      if($data[$k] !~ /<null>/ && $data[$k] !~ /\?/){
	$m_data[$n_temp] = $data[$k];
	$n_temp ++;
      }
    }

    # normalize each attribute and modify class value
    $n_att_zero = 0;
    for($k=0;$k<@m_data;$k++){
      @line = split(/,/,$m_data[$k]);
      for($at=0;$at<@line-1;$at++){
	if($att_kind{"$att_name[$at]"} eq "other"){
	  @temp = split(/,/,$att_other{"$att_name[$at]"});
	  for($ot=0;$ot<@temp;$ot++){
	    if($line[$at] eq $temp[$ot]){
	      $line[$at] = $ot;
	      last;
	    }
	  }
	}
	if(($att_max{"$att_name[$at]"} - $att_min{"$att_name[$at]"}) < 0.00000001){
	  $line[$at] = -99999;
	  if($i == 0 && $j == 0 && $k == 0){
	    $n_att_zero ++;
	  }
	}else{
	  $line[$at] = 
	    ($line[$at] - $att_min{"$att_name[$at]"}) 
	      / ($att_max{"$att_name[$at]"} - $att_min{"$att_name[$at]"});
	}
      }
      $line[$#line] = $class_number{"$line[$#line]"};
      $npc[$line[$#line]] ++;

      $line_temp = ();
      for($at=0;$at<@line-1;$at++){
	if($line[$at] >= 0.0){
	  $line_temp .= "$line[$at],";
	}
      }
      $line_temp .= "$line[$#line]";
      $m_data[$k] = $line_temp;
    }

    # write the modified file
    open(OUT_Info, ">> data_info.txt");

    if($i == 0 && $j == 0){
      if($n_att_zero > 0){
	print "There exist(s) unnecessary attribute(s). #:$n_att_zero\n";
	print OUT_Info "There exist(s) unnecessary attribute(s). #:$n_att_zero\n";
	print OUT_Info "Unnecessary attribute(s) is/are removed.\n\n";
	$n_att -= $n_att_zero;
      }
      print OUT_Info "# of patterns for each class per partition\n";

    }

    $fileout = "a0_"."$i"."_$dataname"."-10"."$filenum2[$j]".".dat";
    open(OUT, "> $fileout");
    $n_ptt = @m_data;
    print OUT "$n_ptt,$n_att,$n_class,\n";
    for($k=0;$k<@m_data;$k++){
      print OUT "$m_data[$k],\n";
    }
    close(OUT);

    print OUT_Info "$i - $filenum2[$j]: ";
    for($k=0;$k<$n_class;$k++){
      print OUT_Info "$npc[$k]\t ";
    }
    print OUT_Info "Total: $n_ptt\n";
    close(OUT_Info);
  }
}


##################################################
############# New Partitions #####################
##################################################

$filein = "a0_0_"."$dataname"."-10tra.dat";
open(IN, " $filein");
@data = <IN>;
close(IN);
($n_ptt, $n_att, $n_class, $gomi) = split(/,/,$data[0]);
for($i=0; $i < $n_ptt; $i ++){
    $PAT[$i] = $data[$i + 1];
}
$filein = "a0_0_"."$dataname"."-10tst.dat";
open(IN, " $filein");
@data = <IN>;
close(IN);
($n_ptt2, $n_att, $n_class, $gomi) = split(/,/,$data[0]);
for($i=0; $i < $n_ptt2; $i ++){
    $PAT[$i + $n_ptt] = $data[$i + 1];
}
$n_ptt += $n_ptt2;

open(OUT, "> all_data.dat");

print OUT "$n_ptt,$n_att,$n_class,\n";
print OUT @PAT;

close(OUT);


for($x=1;$x<5;$x++){
    srand($x);
    @PAT = sort { int(rand(10)) - 1} @PAT;

    for($i=0;$i<@PAT; $i++){
	for($j=$i+1;$j<@PAT;$j++){
	    @line_i = split(/,/,$PAT[$i]);
	    @line_j = split(/,/,$PAT[$j]);
	    if($line_i[$n_att] > $line_j[$n_att]){
		$temp_PAT = $PAT[$j];
		$PAT[$j] = $PAT[$i];
		$PAT[$i] = $temp_PAT;
	    }
	}
    }

    ############# Make 10 CV files ##########

    for($y=0;$y<10;$y++){
	@PAT_tra = ();
	@PAT_tst = ();
	$n_ptt_tra = 0;
	$n_ptt_tst = 0;
	for($i=0; $i<@PAT;$i++){
	    if($i % 10 == $y){
		$PAT_tst[$n_ptt_tst] = $PAT[$i];
		$n_ptt_tst ++;
	    }else{
		$PAT_tra[$n_ptt_tra] = $PAT[$i];
		$n_ptt_tra ++;
	    }
	}

	$fileout = "a"."$x"."_"."$y"."_"."$dataname"."-10tra.dat";
	open(OUT_tra, "> $fileout");
	print OUT_tra "$n_ptt_tra,$n_att,$n_class,\n";
	print OUT_tra @PAT_tra;
	close(OUT_tra);

	$fileout = "a"."$x"."_"."$y"."_"."$dataname"."-10tst.dat";
	open(OUT_tst, "> $fileout");
	print OUT_tst "$n_ptt_tst,$n_att,$n_class,\n";
	print OUT_tst @PAT_tst;
	close(OUT_tst);
    }

}


    
